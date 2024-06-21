package io.crnk.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.crnk.core.engine.document.Resource;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Assert;
import org.junit.Test;

public class OperationTest {


	@Test
	public void testEquals() {
		EqualsVerifier.forClass(Operation.class).usingGetClass()
				// https://github.com/jqno/equalsverifier/issues/486
				.withPrefabValues(JsonNode.class, NullNode.instance, new TextNode("foo"))
				.suppress(Warning.NONFINAL_FIELDS).verify();

	}

	@Test
	public void testHashCode() {
		Operation op1 = new Operation("a", "b", new Resource());
		Operation op2 = new Operation("a", "b", new Resource());
		Operation op3 = new Operation("x", "b", new Resource());
		Assert.assertEquals(op1, op2);
		Assert.assertNotEquals(op3.hashCode(), op2.hashCode());
	}

}
