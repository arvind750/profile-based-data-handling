package org.profiles.Configuration;

import org.profiles.Model.Message;
import org.profiles.Repository.MessageRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public RepositoryItemReader<Message> messageReader(MessageRepository messageRepository) {
        if (activeProfile == null || activeProfile.trim().isEmpty()) {
            throw new IllegalStateException("Active profile is not set. Please specify spring.profiles.active.");
        }
        RepositoryItemReader<Message> reader = new RepositoryItemReader<>();
        reader.setRepository(messageRepository);
        reader.setMethodName("findAllByProfileName");
        reader.setArguments(java.util.Arrays.asList(activeProfile.trim()));
        reader.setSort(new HashMap<String, Sort.Direction>() {{
            put("id", Sort.Direction.ASC);
        }});
        return reader;
    }

    @Bean
    public ItemProcessor<Message, Message> messageProcessor() {
        return message -> {
            if (message.getMessage() != null) {
                logger.info("Processing message ID {}: {} -> {}", message.getId(), message.getMessage(), message.getMessage().toUpperCase());
                message.setMessage(message.getMessage().toUpperCase());
            }
            return message;
        };
    }

    @Bean
    public RepositoryItemWriter<Message> messageWriter(MessageRepository messageRepository) {
        RepositoryItemWriter<Message> writer = new RepositoryItemWriter<>();
        writer.setRepository(messageRepository);
        return writer;
    }

    @Bean
    public Step messageProcessingStep(
            RepositoryItemReader<Message> messageReader,
            ItemProcessor<Message, Message> messageProcessor,
            RepositoryItemWriter<Message> messageWriter,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("messageProcessingStep", jobRepository)
                .<Message, Message>chunk(10, transactionManager)
                .reader(messageReader)
                .processor(messageProcessor)
                .writer(messageWriter)
                .build();
    }

    @Bean
    public Job messageProcessingJob(Step messageProcessingStep, JobRepository jobRepository) {
        return new JobBuilder("messageProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(messageProcessingStep)
                .build();
    }
}