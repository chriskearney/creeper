package com.comandante.creeper.bot.command;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class TwitterManagerTest {

    @Test
    public void testExtractFails() throws Exception {
        Optional<String> s = TwitterManager.extractFirstTwitterUrl("JUST IN: National Guard soldiers have arrived in DC from the 53rd Infantry Brigade Combat Team and have been sworn in as temporary U.S. Marshals in support of citizens at the Nation’s Capital. pic.twitter.com/xfUyIrjjit");
        Assert.assertFalse(s.isPresent());
    }

    @Test
    public void testSucceed() {
        Optional<String> s = TwitterManager.extractFirstTwitterUrl("https://twitter.com/MysteryGrove/status/1269528089303842816");
        Assert.assertEquals(s.get(), "1269528089303842816" );
    }

    @Test
    public void testSucceed2() {
        Optional<String> s = TwitterManager.extractFirstTwitterUrl("https://twitter.com/Acyn/status/1261829390951788544?ref_src=twsrc%5Etfw%7Ctwcamp%5Etweetembed%7Ctwterm%5E1261829390951788544&ref_url=https%3A%2F%2Fwww.vox.com%2F2020%2F5%2F18%2F21262403%2Feric-trump-jeanine-pirro-coronavirus-hoax");
        Assert.assertEquals(s.get(), "1261829390951788544" );
    }


    @Test
    public void testFail() {
        Optional<String> s = TwitterManager.extractFirstTwitterUrl("https://mobile.twitter.com/search?q=%23phillyexplosions&src=trend_click wild");
        Assert.assertFalse(s.isPresent());
    }



    @Test
    public void testSucceed4() {
        Optional<String> s = TwitterManager.extractFirstTwitterUrl("https://twitter.com/GovWhitmer/status/1249536322521399302?ref_src=twsrc%5Etfw%7Ctwcamp%5Etweetembed%7Ctwterm%5E1249536322521399302&ref_url=https%3A%2F%2Fwww.wxyz.com%2Fnews%2Fcoronavirus%2Fsetting-the-record-straight-on-viral-whitmer-photo");
        Assert.assertEquals(s.get(), "1249536322521399302" );
    }
}