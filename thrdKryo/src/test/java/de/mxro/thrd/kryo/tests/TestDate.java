package de.mxro.thrd.kryo.tests;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.DateSerializer;

public class TestDate {

    /**
     * Test due to a bug re date storing in Appjangle cloud.
     * 
     * @throws ParseException
     * @see https
     *      ://groups.google.com/forum/?fromgroups=#!topic/kryo-users/kZacb0ikW98
     */
    @Test
    public void test_serialize_date() throws ParseException {

        final SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yy hh:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("NZDT"));

        final Date expectedDate = dateFormat.parse("22/02/13 11:00");

        final Kryo kryo = new Kryo();
        kryo.setRegistrationOptional(true);
        kryo.register(Date.class, new DateSerializer());

        final ByteBuffer b = ByteBuffer.allocate(100);

        kryo.writeObject(b, expectedDate);

        b.rewind();

        final Date deserialized = kryo.readObject(b, Date.class);

        Assert.assertEquals(expectedDate, deserialized);
    }

}
