/**
 *
 */
package com.ssh.jsch;

import com.google.common.net.HostAndPort;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import com.terminal.Questioner;
import com.terminal.TtyConnector;
import org.apache.log4j.Logger;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class JSchTtyConnector<T extends Channel> implements TtyConnector {
  public static final Logger LOG = Logger.getLogger(JSchTtyConnector.class);

  // Local Dev
  //public static final int DEFAULT_PORT = 22;

  public static final int DEFAULT_PORT = 30000;

  private InputStream myInputStream = null;
  private OutputStream myOutputStream = null;
  private Session mySession;
  private T myChannelShell;
  private AtomicBoolean isInitiated = new AtomicBoolean(false);

  private int myPort = DEFAULT_PORT;

  private String myUser = null;
  private String myHost = null;
  private String myPassword = null;

  private Dimension myPendingTermSize;
  private Dimension myPendingPixelSize;
  private InputStreamReader myInputStreamReader;
  private OutputStreamWriter myOutputStreamWriter;

  public JSchTtyConnector() {

  }

  public JSchTtyConnector(String host, String user, String password) {
    this(host, DEFAULT_PORT, user, password);
  }

  public JSchTtyConnector(String host, int port, String user, String password) {
    this.myHost = host;
    this.myPort = port;
    this.myUser = user;
    this.myPassword = password;
  }

  public void resize(Dimension termSize, Dimension pixelSize) {
    myPendingTermSize = termSize;
    myPendingPixelSize = pixelSize;
    if (myChannelShell != null) {
      resizeImmediately();
    }
  }

  abstract protected void setPtySize(T channel, int col, int row, int wp, int hp);

  private void resizeImmediately() {
    if (myPendingTermSize != null && myPendingPixelSize != null) {
      setPtySize(myChannelShell, myPendingTermSize.width, myPendingTermSize.height, myPendingPixelSize.width, myPendingPixelSize.height);
      myPendingTermSize = null;
      myPendingPixelSize = null;
    }
  }

  public void close() {
    if (mySession != null) {
      mySession.disconnect();
      mySession = null;
      myInputStream = null;
      myOutputStream = null;
    }
  }

  abstract protected T openChannel(Session session) throws JSchException;

  abstract protected void configureChannelShell(T channel);

  public boolean init(Questioner q) {

    getAuthDetails(q);

    try {
      mySession = connectSession(q);
      myChannelShell = openChannel(mySession);
      configureChannelShell(myChannelShell);
      myInputStream = myChannelShell.getInputStream();
      myOutputStream = myChannelShell.getOutputStream();
      myInputStreamReader = new InputStreamReader(myInputStream, "utf-8");
      myChannelShell.connect();
      resizeImmediately();
      return true;
    }
    catch (final IOException e) {
      q.showMessage(e.getMessage());
      LOG.error("Error opening channel", e);
      return false;
    }
    catch (final JSchException e) {
      q.showMessage(e.getMessage());
      LOG.error("Error opening session or channel", e);
      return false;
    }
    finally {
      isInitiated.set(true);
    }
  }

  private Session connectSession(Questioner questioner) throws JSchException {
    JSch jsch = new JSch();
    configureJSch(jsch);

    Session session = jsch.getSession(myUser, myHost, myPort);

    Properties prop = new Properties();
    prop.setProperty("StrictHostKeyChecking", "no");
    session.setConfig(prop);

    final QuestionerUserInfo ui = new QuestionerUserInfo(questioner);
    //ui.promptYesNo()
//    if (myPassword != null) {
//      session.setPassword(myPassword);
//      ui.setPassword(myPassword);
//    }
    session.setUserInfo(ui);

    final java.util.Properties config = new java.util.Properties();
    config.put("compression.s2c", "zlib,none");
    config.put("compression.c2s", "zlib,none");
    configureSession(session, config);
    session.connect();
    session.setTimeout(0);

    return session;
  }
  
  protected void configureJSch(JSch jsch) throws JSchException {
//    public void addIdentity(String name, byte[] prvkey, byte[] pubkey, byte[] passphrase) throws JSchException {
//      Identity identity = IdentityFile.newInstance(name, prvkey, pubkey, this);
//      this.addIdentity((Identity)identity, (byte[])passphrase);
//    }


    // LOCAL DEV
//    final String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
//            "MIIEpAIBAAKCAQEAp8amtK2yhaoIaZT6IE8mrENirOaM9XQRFcLp3c17FtmyWRMA\n" +
//            "RYRcvkJ14qXwXgMkpK9oMJdla3w4tJW6bjldZhk9qSOVTGOaVk3/giw0S8FTRdpU\n" +
//            "3UUFk9Ew1CA5+YrKkSdlJnyzbdnK99z/LPyEa4nmCIHK6GAGdLOaqzqZKK6hct6O\n" +
//            "HuYEyNv8LZziMxG1MnSCeNpKa8DUoufOp+g7vRDXyHHYDNx7lpCl0ul/qtqheQ9E\n" +
//            "+jN9YrtXsUDq7eKdV6EQw5p3IBmlL4alrAAnSsi5Detf0BLvO30dqOrGAsnayv2k\n" +
//            "WoJG9xJW2KFkbWKdtuIqsbJWOsrshEvwsPijGwIDAQABAoIBAAupL+8A35aQsdL8\n" +
//            "ysGyHqL8cABKaeOpdIKJsI18+aAl81JjkrJCN9v1bJIbLHCsUUCs/FXcP+1PjfVn\n" +
//            "LrT4gbhV6sY1tevARHrZIvgeMGhPgIUrXpiecnvKn1UvBDiG3/tkvJAHPMVWzNeb\n" +
//            "YZTz3BRJWlafBsZN3kUAMrDR+tEr7TSZG4+/KAs3MH8XfXBwh9AZy9wTsAhI1DKh\n" +
//            "igQlrFsobI/9Y8iVrr4toSTsWlGduMsevt2TM6xHJ8+NmRxLbndjxbhSp9y7E35w\n" +
//            "nxwJZ17pyOuzBaEhx8qR2V0cxtvXyqpuvuzUaF7HQuNmH2WVA1KR5/cS2jTjJjt7\n" +
//            "mExyK7ECgYEA1VkIRbUu0QJdF9BRyeWjeHko/541oZEBm+mq0tAI8olixXTuHsDm\n" +
//            "6j+hCaxgI7mHLqeqt/rI5BogwUQFgUjM7tCFmMRx+jf2GBD8VzqE+rpAOWudcKdI\n" +
//            "LQMltPBsH58ugbOocF7Qq4Cn8firWy9LC8ihQ5KH5KrjhzzGsah2MIkCgYEAyVFK\n" +
//            "cYE5UlQOKsUK/q2mbMwWoJeQQIuQ8LP4UyR+MmSzUGf5iTBzReyjGdfhzrV39O80\n" +
//            "quEQRKw0EfmaBP4/LBfQYHFW5n0T27eD+x1uKyRHidXmrDbRycmkzYyX1H7/MLm7\n" +
//            "wGZKS/uFyHvdX/qSCh0rWO6PtC0UsAx2QKkqJYMCgYEAoKz90uniSVzbWYz1m/VL\n" +
//            "iPbOzryLLZP0v0Ra94vfwTGA5q7qitTC09Z55LwiZ6VZTuTNaOQBmfCnjIY7EyQN\n" +
//            "2ynGA8inAkhVGcjpEpSrA91zcws+NlJ1xBijtEKBW8tZjisNBxwRUaiCU3TxMKpM\n" +
//            "faJq+WYI3ElrNDWseq4h3OkCgYEAu/FfPU31tK1yOG2SEScOit2RdkVoz0k8e+qY\n" +
//            "Nw6HngYy/SrEZFZOF4aO0e6gCXmSzqCOm8TXijT1u545MtYlJcXprE/DYBR++I1/\n" +
//            "8myMUExjWQPPgREAk5DjgV3y1Nfm55dN95XHYN4lDIDOr+7ebgDDWhgFIqE/ggdw\n" +
//            "UndbOH8CgYArLGkN3JsXktnOaf/uwZQpLeAijEmnU1mYaEo67TO18VMRL7ejZYIC\n" +
//            "AGf/K3wMiR6UB12ApA3IKHGH4NpCqxQ2bpIMnqEJTEBdxPWE5GPYSoaV3PlDvSs9\n" +
//            "FFGz3zveB9FcGzwIEP8g4g4l6aZZCwTvU7bX+N+/s/PJ6OJvrYo0tA==\n" +
//            "-----END RSA PRIVATE KEY-----";

    final String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpAIBAAKCAQEAng//Dz5Ae/+b3I6JrClN2PGtKmXkYN5SPFby4UNniOyaOdPZ\n" +
            "VoiM5TgjIT4YSc/xBftBhFnkNfwkIMQSfUSzK3ayt9JLUEiXA4bS0/K2Z9401JHv\n" +
            "x24ggg5X/wsD3CYQdkvaNJPyYSvEUrw54K3PO0ncAVAz51o0vOeYZ9QcmQ35h2rV\n" +
            "Frt7druqiIqZXBZroe26g8C5p4wQPw9B7NB0F9TaKzpnu06SROq+ylqlLk2lRsyd\n" +
            "NdmMKT482AUcLQI+vdtKckiMMYpeYcWCUthp+INAhLjvKhU+ze65DUGqjte7z2vA\n" +
            "CvosPjkBZW2plYe37TUD/z4cChVixmYK0tGBLQIDAQABAoIBAF0ldhyohd3M8y/V\n" +
            "u+H98UdgnLi4lQ5U1ceDQdxoGXKCZByh8JljHQzTuTgU3MFazmHFAIYbZzY9IZlB\n" +
            "ktfJjYCWkZlsTz+/l2bXpBSNGts5Ao8mdVRkXLnn9Alzl1G8CMV9y9hiO1ryLN7I\n" +
            "157FLrN0wjbv6bZz3VJZdOHzCOtUeJ1PvKkmh42QwqpgfQ/e3JmUYA9NJFcf1ehz\n" +
            "0OpqAHsPPGE0AL65iKkpeNUsmNRyQTb69495j13YiwpyRNZR48ScaV0LHdAsOnVw\n" +
            "2StjIpZsDfSnAQoxBs4wKhqX6xgPpuSVAkWhGDZ1WhXODh5ZQ6WWY/146V7NXiFF\n" +
            "7/Uuwq0CgYEA0m6AYpBMpGSjNVrUgVADF5s1xahPeInihhw5jEaDj8AK78n6dR04\n" +
            "WWm9hSFbxWtzsM/A2TMr2L+cewkbTP0t6ENVfezogYDbnZIT0GzwZvoqWrcr7mCi\n" +
            "5zx1czLZ5Qfbz19igQkpA3Ib57us9o79cBE88P15GvSZHS9ifjBcOqMCgYEAwEpb\n" +
            "CG4IOdrYQk3EC0c8S8VUEt+Or/Z63IG7AejAdfv8UpBjp+jIH1GI25+qmw5nFcB7\n" +
            "a2Jd9EUdr0FOZSxMWmmhypNpQ1KC0K6iuvUrdQAXyjQnZYTp+TyEJfi5W4LKN3Up\n" +
            "HfP8zXWUzRHMtfB9vVSxqYxQF2rUT/B8Mzp1Ye8CgYAPqnhWXiXGi1N3MmblpZ5F\n" +
            "UKHFME2STLmXgFxsbAd5WTO3PFMwCtfaGDwqwBwD64b2X9EcmmmmPkWZB0mIBsU3\n" +
            "KGQh9tQsZ/pxlaFx/9o54F/s1vwnR/x4uJCJ3fxIx7f+jTxZHOR3xDP9oYQz6ttF\n" +
            "T5M44bX1YsZPXOq5OEJ1fwKBgQC8hencSOx9tGbEErQ7Dns6GlwEKPQe5nusRvCO\n" +
            "vaA7zHKki/V4gMv7kJeqI09DuAovFEisjoNo4n5o/ZEbtiOhnODH2GCiZXnlmOHo\n" +
            "hEg37IBmeV2KtZYjCkbRZ5pq8r7JQm+uczCOS1I4/9OBKShOAIQyo2M+ojlHqpJK\n" +
            "M200NwKBgQCpyUuRGQi3ck7FK2jlSBiHbhkMlLYxyGalIYenbW35+wWPtHEOmGDg\n" +
            "c8WXDhR6hQEBT4E23Kx1plDEcLAr8Z8Z5s+OcZTsvfP5+qcCoQjAryV+5Z4hMlb8\n" +
            "E1TWC/o4d36fC+3fPCx3krJ/JHy0XkOXgq7HjWtuu9VXKvPwIqeQ1g==\n" +
            "-----END RSA PRIVATE KEY-----";

    // LOCAL DEV
//    final String publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCnxqa0rbKFqghplPogTyasQ2Ks5oz1dBEVwun" +
//            "dzXsW2bJZEwBFhFy+QnXipfBeAySkr2gwl2VrfDi0lbpuOV1mGT2pI5VMY5pWTf+CLDRLwVNF2lTdRQWT0TDUIDn5isqRJ" +
//            "2UmfLNt2cr33P8s/IRrieYIgcroYAZ0s5qrOpkorqFy3o4e5gTI2/wtnOIzEbUydIJ42kprwNSi586n6Du9ENfIcdgM3Hu" +
//            "WkKXS6X+q2qF5D0T6M31iu1exQOrt4p1XoRDDmncgGaUvhqWsACdKyLkN61/QEu87fR2o6sYCydrK/aRagkb3ElbYoWRtY" +
//            "p224iqxslY6yuyES/Cw+KMb bridge@chrisk";

    final String publicKey  = "com.ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCeD/8PPkB7/5vcjomsKU3Y8a0qZeR" +
            "g3lI8VvLhQ2eI7Jo509lWiIzlOCMhPhhJz/EF+0GEWeQ1/CQgxBJ9RLMrdrK30ktQSJcDhtLT8" +
            "rZn3jTUke/HbiCCDlf/CwPcJhB2S9o0k/JhK8RSvDngrc87SdwBUDPnWjS855hn1ByZDfmHatUW" +
            "u3t2u6qIiplcFmuh7bqDwLmnjBA/D0Hs0HQX1NorOme7TpJE6r7KWqUuTaVGzJ012YwpPjzYBR" +
            "wtAj6920pySIwxil5hxYJS2Gn4g0CEuO8qFT7N7rkNQaqO17vPa8AK+iw+OQFlbamVh7ftNQP/P" +
            "KFWLGZgrS0YEt bridge@creeper";

    final String passPhrase = "";

    byte[] privateKeyBytes = privateKey.getBytes();
    byte[] publicKeyBytes = publicKey.getBytes();
    byte[] passPhraseBytes = myPassword.getBytes();
    KeyPair load = KeyPair.load(jsch, privateKeyBytes, publicKeyBytes);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    load.writePrivateKey(byteArrayOutputStream);

    //jsch.addIdentity("creeper.ktwit.net", byteArrayOutputStream.toByteArray(), load.getPublicKeyBlob(), new byte[0]);

    //LocalDev
     jsch.addIdentity("creeper.ktwit.net", byteArrayOutputStream.toByteArray(), load.getPublicKeyBlob(), new byte[0]);

  }

  protected void configureSession(Session session, final java.util.Properties config) throws JSchException {
    session.setConfig(config);
    session.setTimeout(5000);

  }

  private void getAuthDetails(Questioner q) {
    while (true) {
      if (myHost == null) {
        myHost = q.questionVisible("host: ", "localhost");
      }
      if (myHost == null || myHost.length() == 0) {
        continue;
      }

      try {
        HostAndPort hostAndPort = HostAndPort.fromString(myHost);
        myHost = hostAndPort.getHostText();
        // override myPort only if specified in the input
        myPort = hostAndPort.getPortOrDefault(myPort);
      } catch (IllegalArgumentException e) {
        q.showMessage(e.getMessage());
        myHost = q.questionVisible("host: ", myHost);
        continue;
      }

      if (myUser == null) {
        myUser = q.questionVisible("user: ", System.getProperty("user.name").toLowerCase());
      }
      if (myUser == null || myUser.length() == 0) {
        continue;
      }
      break;
    }
  }

  public String getName() {
    return myHost != null ? myHost : "Remote";
  }

  @Override
  public int read(char[] buf, int offset, int length) throws IOException {
    return myInputStreamReader.read(buf, offset, length);
  }

  public int read(byte[] buf, int offset, int length) throws IOException {
    return myInputStream.read(buf, offset, length);
  }

  public void write(byte[] bytes) throws IOException {
    if (myOutputStream != null) {
      myOutputStream.write(bytes);
      myOutputStream.flush();
    }
  }

  @Override
  public boolean isConnected() {
    return myChannelShell != null && myChannelShell.isConnected();
  }

  @Override
  public void write(String string) throws IOException {
    write(string.getBytes("utf-8")); //TODO: fix
  }

  @Override
  public int waitFor() throws InterruptedException {
    while (!isInitiated.get() || isRunning(myChannelShell)) {
      Thread.sleep(100); //TODO: remove busy wait
    }
    return myChannelShell.getExitStatus();
  }

  private static boolean isRunning(Channel channel) {
    return channel != null && channel.getExitStatus() < 0 && channel.isConnected();
  }

}
