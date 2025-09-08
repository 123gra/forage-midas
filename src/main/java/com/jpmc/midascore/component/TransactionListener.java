package com.jpmc.midascore.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionListener {

    @Value("${general.kafka-topic}")
    private String topicName;
    @Value("${incentive.api-url}")
    private String incentiveApiUrl;

    @Autowired /// this asks spring to give instance of the repositories so it can communicate
               /// with database
    private UserRepository userrep;
    @Autowired
    private TransactionRecordRepository tranRecRep;
    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "transaction-listener")
    public void listen(Transaction transaction) {
        System.out.println("===Ahsan===");
        System.out.println("Received transaction: " + transaction);

        UserRecord sender = userrep.findById(transaction.getSenderId());
        UserRecord receiver = userrep.findById(transaction.getRecipientId());

        if (sender == null || receiver == null) {
            System.out.println("Invalid sender or receiver skipping this transaction");
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("Sender does not have enuf money");
            return;
        }

        System.out.println("Sending transaction to Incentive API: " + transaction);
        Incentive incentive = restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
        System.out.println("Received Incentive object: " + incentive);


        float incAmm = (float) 0.0;

        if(incentive!=null){
            incAmm = (float) incentive.getAmount();
            System.out.println("Incentive amount received: " + incentive.getAmount());
        }

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        receiver.setBalance(receiver.getBalance() + transaction.getAmount()+incAmm);

        userrep.save(sender);
        userrep.save(receiver);

        TransactionRecord rec = new TransactionRecord(sender, receiver, transaction.getAmount());
        tranRecRep.save(rec);

        System.out.println("Transaction Completed");

    }

}
