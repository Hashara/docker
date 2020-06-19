package com.example.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DockerApplication {
//    DefaultDockerClientConfig.Builder config
//            = DefaultDockerClientConfig.createDefaultConfigBuilder();
//    DockerClient dockerClient = DockerClientBuilder
//            .getInstance(config)
//            .build();

    public static void main(String[] args) {
        SpringApplication.run(DockerApplication.class, args);
    }

}
