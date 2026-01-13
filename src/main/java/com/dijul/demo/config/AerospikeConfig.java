package com.dijul.demo.config;



import com.aerospike.client.Host;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

import java.util.Collection; // Updated Import
import java.util.Collections;

@Configuration
@EnableAerospikeRepositories(basePackages = "com.dijul.demo.repo")
public class AerospikeConfig extends AbstractAerospikeDataConfiguration {

    @Override
    protected Collection<Host> getHosts() { // Changed from List to Collection
        return Collections.singleton(new Host("localhost", 3000));
    }
    protected String getNamespace() {
        return "test";
    }


}