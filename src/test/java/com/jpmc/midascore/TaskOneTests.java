package com.jpmc.midascore;

// 📦 Importing required testing and logging components
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ✅ TaskOneTests verifies successful application startup.
 * It also generates a deterministic output for task submission.
 */
@SpringBootTest
class TaskOneTests
{
    // 📝 Logger for structured output
    static final Logger logger = LoggerFactory.getLogger(TaskOneTests.class);

    /**
     * 🧪 task_one_verifier confirms that the Spring Boot application boots correctly.
     * It waits briefly, then logs a formatted output block for task validation.
     */
    @Test
    void task_one_verifier() throws InterruptedException
    {
        // ⏳ Pause to simulate startup delay
        Thread.sleep(2000);

        // 🎉 Confirmation message
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("Congrats! It looks like your application booted without issue");
        logger.info("submit the following output to complete the task (include begin and end output denotations)");

        // 📦 Generate deterministic output block
        StringBuilder output = new StringBuilder("\n").append("---begin output ---").append("\n");
        for (int i = 0; i < 10; i++)
        {
            output.append((int) Math.floor(Math.pow(i, i)));
        }
        output.append("\n").append("---end output ---");

        // 🖨️ Log the final output
        logger.info(output.toString());
    }
}