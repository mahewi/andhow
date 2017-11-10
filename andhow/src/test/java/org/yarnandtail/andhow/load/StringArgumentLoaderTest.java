package org.yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * Note:  This directly tests a single loader so it is not possible to
 * test for missing required values.  Loaders can't know if a value is missing -
 * that only can be figured out after all loaders are comlete.
 * 
 * @author eeverman
 */
public class StringArgumentLoaderTest {
	
	StaticPropertyConfigurationMutable appDef;
	PropertyValuesWithContextMutable appValuesBuilder;
	
	public interface SimpleParams {
		//Strings
		StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
		StrProp STR_NULL = StrProp.builder().aliasInAndOut("String_Null").build();
		StrProp STR_ENDS_WITH_XXX = StrProp.builder().mustEndWith("XXX").build();


		//Flags
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
		FlagProp FLAG_NULL = FlagProp.builder().build();
	}

	@Before
	public void init() throws Exception {
		appValuesBuilder = new PropertyValuesWithContextMutable();
		
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		GroupProxy proxy = AndHowUtil.buildGroupProxy(SimpleParams.class);
		
		appDef = new StaticPropertyConfigurationMutable(bns);
		appDef.addProperty(proxy, SimpleParams.STR_BOB);
		appDef.addProperty(proxy, SimpleParams.STR_NULL);
		appDef.addProperty(proxy, SimpleParams.STR_ENDS_WITH_XXX);
		appDef.addProperty(proxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(proxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(proxy, SimpleParams.FLAG_NULL);

	}
	
	@Test
	public void testCmdLineLoaderHappyPathAsList() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + StringArgumentLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + StringArgumentLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + StringArgumentLoader.KVP_DELIMITER + "XXX");
		args.add(basePath + "FLAG_TRUE" + StringArgumentLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + StringArgumentLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + StringArgumentLoader.KVP_DELIMITER + "true");
		
		
		StringArgumentLoader cll = new StringArgumentLoader(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("XXX", result.getExplicitValue(SimpleParams.STR_ENDS_WITH_XXX));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testCmdLineLoaderHappyPathAsArray() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + StringArgumentLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + StringArgumentLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + StringArgumentLoader.KVP_DELIMITER + "something_XXX");
		args.add(basePath + "FLAG_TRUE" + StringArgumentLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + StringArgumentLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + StringArgumentLoader.KVP_DELIMITER + "true");
		
		
		StringArgumentLoader cll = new StringArgumentLoader(args.toArray(new String[5]));
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("something_XXX", result.getExplicitValue(SimpleParams.STR_ENDS_WITH_XXX));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	

	@Test
	public void testCmdLineLoaderEmptyValues() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + StringArgumentLoader.KVP_DELIMITER + "");
		args.add(basePath + "STR_NULL" + StringArgumentLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_TRUE" + StringArgumentLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_FALSE" + StringArgumentLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_NULL" + StringArgumentLoader.KVP_DELIMITER + "");
		
		StringArgumentLoader cll = new StringArgumentLoader(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testInvalidPropertyValuesAreNotCheckedByLoaders() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_ENDS_WITH_XXX" + StringArgumentLoader.KVP_DELIMITER + "something_YYY");
		
		
		StringArgumentLoader cll = new StringArgumentLoader(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
	}
	
	@Test
	public void testCmdLineLoaderDuplicateValuesAndSpaces() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_NULL" + StringArgumentLoader.KVP_DELIMITER + "1");
		args.add(basePath + "STR_NULL" + StringArgumentLoader.KVP_DELIMITER + "2");
		args.add(basePath + "STR_NULL" + StringArgumentLoader.KVP_DELIMITER + "3");
		args.add(basePath + "FLAG_NULL" + StringArgumentLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + StringArgumentLoader.KVP_DELIMITER + "false");
		
		
		StringArgumentLoader cll = new StringArgumentLoader(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(3, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	@Test
	public void testCmdLineLoaderWithUnknownProperties() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "XXX" + StringArgumentLoader.KVP_DELIMITER + "1");
		args.add(basePath + "YYY" + StringArgumentLoader.KVP_DELIMITER + "2");
		
		
		StringArgumentLoader cll = new StringArgumentLoader(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(2, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

}
