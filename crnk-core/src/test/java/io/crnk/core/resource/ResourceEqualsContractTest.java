package io.crnk.core.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.crnk.core.engine.document.Resource;
import io.crnk.core.engine.document.ResourceIdentifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class ResourceEqualsContractTest {

	@Test
	public void testResourceIdEqualsContract() {
        EqualsVerifier.forClass(ResourceIdentifier.class)
                .usingGetClass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
	}

	@Test
	public void testResourceEqualsContract() {
         EqualsVerifier.forClass(Resource.class)
                .usingGetClass()
                .suppress(Warning.NONFINAL_FIELDS)
                // https://github.com/jqno/equalsverifier/issues/486
                .withPrefabValues(JsonNode.class, NullNode.instance, new TextNode("foo"))
                .verify();
	}

}
