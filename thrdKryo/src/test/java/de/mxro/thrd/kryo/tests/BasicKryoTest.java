package de.mxro.thrd.kryo.tests;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;

public class BasicKryoTest {

	public static class Person {
		protected String name;

		public Person(final String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Deprecated
		public Person() {

		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Person other = (Person) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		
	}

	@Test
	public void serialization_deserialization_test() throws Exception {
		final Kryo kryo = new Kryo();
		kryo.setRegistrationOptional(true);
		kryo.register(Person.class);

		final ByteBuffer b = ByteBuffer.allocate(100);

		final Person someObject = new Person("Max");

		kryo.writeObject(b, someObject);

		b.rewind();

		final Person deserialized = kryo.readObject(b, Person.class);

		Assert.assertEquals(someObject, deserialized);
	}
}
