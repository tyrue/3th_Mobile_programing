package com.kakao.util.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2017. 2. 23..
 */
public class FileUtilsTest {
    @Test
    public void testToFileName() {
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc\"def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc*def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc/def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc:def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc<def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc>def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc?def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc\\def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
        Assert.assertEquals("abc" + FileUtils.FILE_NAME_AVAIL_CHARACTER + "def", FileUtils.toFileName("abc|def", FileUtils.FILE_NAME_AVAIL_CHARACTER));
    }
}
