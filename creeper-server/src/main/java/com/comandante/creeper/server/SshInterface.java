package com.comandante.creeper.server;

import org.apache.sshd.common.config.keys.OpenSshCertificate;
import org.apache.sshd.common.keyprovider.FileHostKeyCertificateProvider;
import org.apache.sshd.common.keyprovider.HostKeyCertificateProvider;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.hostbased.HostBasedAuthenticator;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.shell.ShellFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

public class SshInterface {

    public void configureServer() throws Exception {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setPort(8989);
        sshd.setShellFactory(new ShellFactory() {
            @Override
            public Command createShell(ChannelSession channelSession) throws IOException {
                return null;
            }
        });
        sshd.setPasswordAuthenticator(new MyPasswordAuthenticator());

        sshd.start();
    }

}
