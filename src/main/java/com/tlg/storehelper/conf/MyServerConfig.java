package com.tlg.storehelper.conf;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyServerConfig {

    @Bean
    @Primary
    public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> webServerFactoryCustomizer(){
        return new WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>() {
            @Override
            public void customize(ConfigurableTomcatWebServerFactory factory) {
                factory.setPort(8080);
                factory.addConnectorCustomizers(connector -> ((AbstractHttp11Protocol) connector.getProtocolHandler()).setMaxKeepAliveRequests(1),
                        connector -> ((AbstractHttp11Protocol) connector.getProtocolHandler()).setConnectionTimeout(20000),
                        connector -> ((AbstractHttp11Protocol) connector.getProtocolHandler()).setMaxThreads(1000),
                        connector -> ((AbstractHttp11Protocol) connector.getProtocolHandler()).setMaxHttpHeaderSize(81920),
                        connector -> ((AbstractHttp11Protocol) connector.getProtocolHandler()).setMaxConnections(1000));
            }
        };
    }

}
