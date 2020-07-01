package com.comandante.creeper.bot.command;

import com.comandante.creeper.api.ClientConnectionInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

public class AccuweatherAPITest {


    private String clientPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
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

    private String clientPublicKey = "com.ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCeD/8PPkB7/5vcjomsKU3Y8a0qZeR" +
            "g3lI8VvLhQ2eI7Jo509lWiIzlOCMhPhhJz/EF+0GEWeQ1/CQgxBJ9RLMrdrK30ktQSJcDhtLT8" +
            "rZn3jTUke/HbiCCDlf/CwPcJhB2S9o0k/JhK8RSvDngrc87SdwBUDPnWjS855hn1ByZDfmHatUW" +
            "u3t2u6qIiplcFmuh7bqDwLmnjBA/D0Hs0HQX1NorOme7TpJE6r7KWqUuTaVGzJ012YwpPjzYBR" +
            "wtAj6920pySIwxil5hxYJS2Gn4g0CEuO8qFT7N7rkNQaqO17vPa8AK+iw+OQFlbamVh7ftNQP/P" +
            "KFWLGZgrS0YEt bridge@creeper";

    private String sshHostname = "creeper.ktwit.net";
    private int sshPort = 30000;
    private String sshUser = "bridge";
    private String sshPass = "";

    private String clientPassPhrase = "";

    private String clientConnectHostname = "creeper.ktwit.net";


    @Test
    public void testClientConnectInfo() throws JsonProcessingException {
        ClientConnectionInfo clientConnectionInfo = new ClientConnectionInfo(clientPrivateKey, clientPublicKey, clientPassPhrase, clientConnectHostname, sshHostname, sshPort, sshUser, sshPass);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        String s = objectMapper.writeValueAsString(clientConnectionInfo);
        System.out.println(s);
    }

}