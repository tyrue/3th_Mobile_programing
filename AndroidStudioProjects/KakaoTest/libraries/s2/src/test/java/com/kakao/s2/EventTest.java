package com.kakao.s2;

import android.os.Parcel;

import com.kakao.util.exception.KakaoException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2016. 8. 24..
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class EventTest {

    private final String to = "http://www.kakaocorp.com";
    private final String action = "click";
    @Test
    @SuppressWarnings("unused")
    public void testBuilderWithNoField() {
        try {
            Event event = new Event.Builder().build();
            Assert.fail("An empty event creation should fail");
        } catch (KakaoException e) {
            Assert.assertEquals(KakaoException.ErrorType.ILLEGAL_ARGUMENT, e.getErrorType());
        }
    }

    @Test
    public void testBuilderWithAllFields() {
        long timestamp = new Date().getTime();
        Event event = new Event.Builder().setTimestamp(timestamp).setTo(to).setAction(action).build();
        assertEventEquals(event, timestamp, null, to, action);
    }

    @Test
    public void testBuilderWithNoAppKey() {
        long timestamp = new Date().getTime();
        Event event = new Event.Builder().setTimestamp(timestamp).setTo(to).setAction(action).build();
        assertEventEquals(event, timestamp, null, to, action);
    }

    @Test
    public void parcelable() {
        Event event = new Event.Builder().setTimestamp(new Date().getTime()).setTo(to).setAction(action).build();
        testParcelableWithEvent(event);

        Map<String, Object> props = new HashMap<>();
        props.put("key", "value");
        Event evennt2 = new Event.Builder().setTimestamp(new Date().getTime()).setTo(to).setAction(action).setAdidEnabled(true).setProps(props).build();
        testParcelableWithEvent(evennt2);

    }

    private void assertEventEquals(Event event, long timestamp, String from, String to, String action) {
        Assert.assertEquals(event.getTo(), to);
        Assert.assertEquals(event.getFrom(), from);
        Assert.assertEquals(event.getAction(), action);
        Assert.assertEquals(event.getTimestamp(), timestamp, 0);
    }

    private void testParcelableWithEvent(final Event event) {
        Parcel parcel = Parcel.obtain();
        event.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Event retrieved = Event.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(event, retrieved);
    }
}
