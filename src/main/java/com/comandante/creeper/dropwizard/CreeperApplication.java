package com.comandante.creeper.dropwizard;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.api.ApiResource;
import com.comandante.creeper.player.Player;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class CreeperApplication extends Application<CreeperConfiguration> {

    public static void main(String[] args) throws Exception {
        new CreeperApplication().run(args);
    }

    @Override
    public String getName() {
        return "creeper";
    }

    @Override
    public void initialize(Bootstrap<CreeperConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(CreeperConfiguration configuration, Environment environment) {
        try {
            Creeper creeper = new Creeper(configuration);
            creeper.startAsync().awaitRunning();


            environment.jersey().register(new AuthDynamicFeature(
                    new BasicCredentialAuthFilter.Builder<Player>()
                            .setAuthenticator(new CreeperBasicAuthenticator(creeper.getPlayerManager()))
                            .setAuthorizer(new CreeperAuthorizer())
                            .setRealm("SUPER SECRET STUFF")
                            .buildAuthFilter()));
            environment.jersey().register(RolesAllowedDynamicFeature.class);
            //If you want to use @Auth to inject a custom Principal type into your resource
            environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Player.class));

            environment.jersey().register(new ApiResource(creeper.getGameManager()));


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
