package com.comandante.creeper.api;

public class ClientConnectionInfo {

    private String privateKey;
    private String publicKey;
    private String passPhrase;
    private String apiHostname;
    private String sshHostname;
    private int sshPort;
    private String sshUser;
    private String sshPass;

    public ClientConnectionInfo(String privateKey, String publicKey, String passPhrase, String apiHostname, String sshHostname, int sshPort, String sshUser, String sshPass) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.passPhrase = passPhrase;
        this.apiHostname = apiHostname;
        this.sshHostname = sshHostname;
        this.sshPort = sshPort;
        this.sshUser = sshUser;
        this.sshPass = sshPass;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public String getApiHostname() {
        return apiHostname;
    }

    public String getSshHostname() {
        return sshHostname;
    }

    public int getSshPort() {
        return sshPort;
    }

    public String getSshUser() {
        return sshUser;
    }

    public String getSshPass() {
        return sshPass;
    }
}
