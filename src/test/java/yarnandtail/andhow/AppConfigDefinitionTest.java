package yarnandtail.andhow;

import static org.junit.Assert.*;
import org.junit.Test;
import yarnandtail.andhow.appconfig.AppConfigDefinition;
import yarnandtail.andhow.name.AsIsAliasNamingStrategy;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AppConfigDefinitionTest {
	
	String paramFullPath = SimpleParamsWAlias.class.getCanonicalName() + ".";
	
	@Test
	public void testHappyPath() {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, 
				bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));

		//Canonical Names for Point
		assertEquals(paramFullPath + "KVP_BOB", appDef.getCanonicalName(SimpleParamsWAlias.KVP_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParamsWAlias.FLAG_FALSE));
		
		//Get points for Canonical name and alias
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + "KVP_BOB"));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + "FLAG_FALSE"));
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + 
				SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0)));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + 
				SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0)));
		
		//Groups
		assertEquals(1, appDef.getGroups().size());
		assertEquals(SimpleParamsWAlias.class, appDef.getGroups().get(0));
		
		//Point list
		assertEquals(2, appDef.getPoints().size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoints().get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoints().get(1));
		
		//Points for Group
		assertEquals(2, appDef.getPointsForGroup(SimpleParamsWAlias.class).size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(1));
		assertEquals(0, appDef.getPointsForGroup(SimpleParamsNoAlias.class).size());		//A random group that is not registered 
	}
	
	@Test(expected=ConstructionException.class)
	public void testDuplicatePoint() {
		
		NamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsNoAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));

	}
	
	@Test
	public void testDuplicateAlias() {
		String dupParamFullPath = SimpleParamsWAliasDuplicate.class.getCanonicalName() + ".";
		
		//Use Aliases as-is to cause naming collisions
		NamingStrategy bns = new AsIsAliasNamingStrategy();
		AppConfigDefinition appDef = new AppConfigDefinition();
		
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, 
				bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, 
				bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));
		
		// THIS ONE WILL HAVE A DUPLICATE NAME
		appDef.addPoint(SimpleParamsWAliasDuplicate.class, SimpleParamsWAliasDuplicate.FLAG_FALSE, 
				bns.buildNames(SimpleParamsWAliasDuplicate.FLAG_FALSE, SimpleParamsWAliasDuplicate.class, "FLAG_FALSE"));
		// DUPLICATE
		
		//Continue w/ a non-duplicate, which should be added as normal
		appDef.addPoint(SimpleParamsWAliasDuplicate.class, SimpleParamsWAliasDuplicate.FLAG_TRUE, 
				bns.buildNames(SimpleParamsWAliasDuplicate.FLAG_TRUE, SimpleParamsWAliasDuplicate.class, "FLAG_TRUE"));

		//We should have created one NamingException
		assertEquals(1, appDef.getNamingExceptions().size());
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_FALSE, appDef.getNamingExceptions().get(0).getNewPoint());
		assertEquals(dupParamFullPath + "FLAG_FALSE", appDef.getNamingExceptions().get(0).getNewPointCanonName());
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_FALSE.getBaseAliases().get(0), appDef.getNamingExceptions().get(0).getNewPointConflictName());
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getNamingExceptions().get(0).getExistingPoint());
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getNamingExceptions().get(0).getExistingPointCanonName());
		
		//Canonical Names for Point
		assertEquals(paramFullPath + "KVP_BOB", appDef.getCanonicalName(SimpleParamsWAlias.KVP_BOB));
		assertEquals(paramFullPath + "FLAG_FALSE", appDef.getCanonicalName(SimpleParamsWAlias.FLAG_FALSE));
		assertNull(appDef.getCanonicalName(SimpleParamsWAliasDuplicate.FLAG_FALSE));		//not added b/c dup
		assertEquals(dupParamFullPath + "FLAG_TRUE", appDef.getCanonicalName(SimpleParamsWAliasDuplicate.FLAG_TRUE));
		
		//Get points for Canonical name and alias
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(paramFullPath + "KVP_BOB"));
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoint(SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0)));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(paramFullPath + "FLAG_FALSE"));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoint(SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0)));
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPoint(dupParamFullPath + "FLAG_TRUE"));
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPoint(SimpleParamsWAliasDuplicate.FLAG_TRUE.getBaseAliases().get(0)));
		
		//Groups
		assertEquals(2, appDef.getGroups().size());
		assertEquals(SimpleParamsWAlias.class, appDef.getGroups().get(0));
		assertEquals(SimpleParamsWAliasDuplicate.class, appDef.getGroups().get(1));
		
		//Point list
		assertEquals(3, appDef.getPoints().size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPoints().get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPoints().get(1));
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPoints().get(2));
		
		//Points for Group
		assertEquals(2, appDef.getPointsForGroup(SimpleParamsWAlias.class).size());
		assertEquals(SimpleParamsWAlias.KVP_BOB, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(0));
		assertEquals(SimpleParamsWAlias.FLAG_FALSE, appDef.getPointsForGroup(SimpleParamsWAlias.class).get(1));
		assertEquals(1, appDef.getPointsForGroup(SimpleParamsWAliasDuplicate.class).size());
		assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, appDef.getPointsForGroup(SimpleParamsWAliasDuplicate.class).get(0));
		assertEquals(0, appDef.getPointsForGroup(SimpleParamsNoAlias.class).size());		//A random group that is not registered 
	}
}