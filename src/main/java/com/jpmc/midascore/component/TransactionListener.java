package com.jpmc.midascore.component;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;
    @Autowired
    private RestTemplate restTemplate;



    @KafkaListener(topics = "${general.kafka-topic}", groupId = "test-group")
    @Transactional
    public void listen(Transaction ts){

        Optional<UserRecord> senderOpt = userRepository.findById(ts.getSenderId());
        Optional<UserRecord> receiverOpt = userRepository.findById(ts.getRecipientId());



        if(senderOpt.isEmpty() || receiverOpt.isEmpty()){
            return;

        }else{

            UserRecord senderDetails = senderOpt.get();
            UserRecord recipientDetails = receiverOpt.get();
            BigDecimal currentBalance = receiverOpt.get().getBalance();



            if (senderDetails.getBalance().compareTo(ts.getAmount()) >= 0) {

                ResponseEntity<Incentive> response = restTemplate.postForEntity(
                        "http://localhost:8080/incentive",
                        ts,
                        Incentive.class
                );
                BigDecimal incentiveAmount = response.getBody().getAmount();

                BigDecimal newBalance = currentBalance.add(ts.getAmount()).add(incentiveAmount);

                recipientDetails.setBalance(newBalance);

                BigDecimal updatedSenderBalance = senderDetails.getBalance().subtract(ts.getAmount());

                userRepository.save(recipientDetails);


                senderDetails.setBalance(updatedSenderBalance);

                userRepository.save(senderDetails);

                TransactionRecord tr = new TransactionRecord();
                tr.setRecipient(recipientDetails);
                tr.setSender(senderDetails);
                tr.setAmount(ts.getAmount());
                tr.setIncentive(incentiveAmount);
                transactionRecordRepository.save(tr);


                System.out.println("Sender Name: " + senderDetails.getName());
                System.out.println("Recipient Name: " + recipientDetails.getName());
                System.out.println("Recipient Amount: " + recipientDetails.getBalance());

                BigDecimal wilburBalance = getWilburBalance();
                System.out.println("Wilbur's balance (rounded down): " + wilburBalance);









            } else {

                return;
            }





        }





        System.out.println("Received Transactions: " + ts);

    }

    public BigDecimal getWilburBalance() {
        Optional<UserRecord> wilburOpt = userRepository.findByName("wilbur");
        if (wilburOpt.isPresent()) {
            return wilburOpt.get().getBalance().setScale(0, BigDecimal.ROUND_DOWN);
        } else {
            return BigDecimal.ZERO;
        }
    }



}
