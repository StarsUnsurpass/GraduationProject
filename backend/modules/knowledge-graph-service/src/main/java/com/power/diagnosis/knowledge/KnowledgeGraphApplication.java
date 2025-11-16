package com.power.diagnosis.knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * 知识图谱服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.power.diagnosis.knowledge", "com.power.diagnosis.common"})
@EnableNeo4jRepositories(basePackages = "com.power.diagnosis.knowledge.repository")
public class KnowledgeGraphApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeGraphApplication.class, args);
        System.out.println("========================================");
        System.out.println("知识图谱服务启动成功！");
        System.out.println("========================================");
    }
}
