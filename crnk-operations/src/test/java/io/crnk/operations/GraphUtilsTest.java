package io.crnk.operations;

import io.crnk.operations.internal.GraphUtils;
import io.crnk.operations.internal.OperationParameterUtils;
import io.crnk.test.mock.ClassTestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphUtilsTest {


	@Test
	public void testNodeWithoutEdge() {
		GraphUtils.Node node = new GraphUtils.Node("a", null);
		List<GraphUtils.Node> results = GraphUtils.sort(Arrays.asList(node));
		Assert.assertSame(node, results.get(0));
	}

	@Test
	public void testTwoNodesWithoutEdge() {
		GraphUtils.Node a = new GraphUtils.Node("a", null);
		GraphUtils.Node b = new GraphUtils.Node("b", null);
		List<GraphUtils.Node> results = GraphUtils.sort(Arrays.asList(a, b));
		Assert.assertSame(a, results.get(0));
		Assert.assertSame(b, results.get(1));
	}

	@Test
	public void testHasPrivateConstructor() {
		ClassTestUtils.assertPrivateConstructor(GraphUtils.class);
	}

	@Test
	public void testNodeEquals() {
		final GraphUtils.Edge edge1 = new GraphUtils.Edge(new GraphUtils.Node("from1", null), new GraphUtils.Node("to1", null));
		final GraphUtils.Edge edge2 = new GraphUtils.Edge(new GraphUtils.Node("from2", null), new GraphUtils.Node("to2", null));

		EqualsVerifier.forClass(GraphUtils.Node.class).usingGetClass()
				.suppress(Warning.STRICT_INHERITANCE)
				.withPrefabValues(GraphUtils.Edge.class, edge1, edge2)
				.withIgnoredFields("value", "inEdges", "outEdges") // ignore unused fields in equals and hashcode
				.suppress(Warning.NULL_FIELDS).verify();
	}


	@Test
	public void testNodeToString() {
		GraphUtils.Node node = new GraphUtils.Node("test", null);
		Assert.assertEquals("test", node.toString());
	}

	@Test
	public void testEdgeEquals() {
		EqualsVerifier.forClass(GraphUtils.Edge.class).usingGetClass()
				.suppress(Warning.NULL_FIELDS)
				.withPrefabValues(GraphUtils.Node.class, new GraphUtils.Node("node1", null), new GraphUtils.Node("node2", null))
				.verify();
	}

	@Test
	public void testParseSingleParameter() {
		Map<String, Set<String>> map = OperationParameterUtils.parseParameters("test?a=b");
		Assert.assertEquals(1, map.size());
		Assert.assertEquals("b", map.get("a").iterator().next());
		Assert.assertEquals(1, map.get("a").size());
	}

}
