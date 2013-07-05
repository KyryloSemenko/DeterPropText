package test.java.cz.semenko.doclet;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import cz.semenko.doclet.XrefLink;
import cz.semenko.word.ApplicationContextProvider;

/**
 * <p>Vytvořit doclet cz.semenko.doclet.XrefLink pro javaDoc, který zajistí:
 * <ul>
 * <li> Vytvoří v javaDoc odkazy na xref dokumentaci
 * <li> Odkazy budou mít obrázek a alternativní text
 * <li> javaDoc bude odkazovat na xref třídy a na jednotlivé metody pomocí anchor s číslem řádku, korespondujícím metodě<br>
 * například {@link <a href="http://maven.apache.org/plugins/maven-javadoc-plugin/xref/org/apache/maven/plugin/javadoc/JavadocJar.html#154">http://maven.apache.org/plugins/maven-javadoc-plugin/xref/org/apache/maven/plugin/javadoc/JavadocJar.html#154</a>}
 * <li> V pom.xml bude nastaven doclet, text odkazu, obrázek odkazu a mapování javaDoc na xrefDoc
 * <li> Mapování bude obsahovat String hodnotu replacedSubstring a String hodnotu replacementValue
 * <li> Například replacedSubstring=apidocs, replacementValue=xref<br>
 * Pak odkaz <br>
 * http://maven.apache.org/plugins/maven-javadoc-plugin/<b>apidocs</b>/index.html<br> 
 * bude změnen na<br>
 * http://maven.apache.org/plugins/maven-javadoc-plugin/<b>xref</b>/index.html 
 * <li> Když to půjde, při validaci doclet zkontroluje, zda existuje soubor a anchor na který odkazuje, a vyhodí Warning kdyz neexistuje
 * <li> Nakonfigurovat XrefLink do Mavenu 
 * </ul>
 * 
 * 
 * @author Kyrylo Semenko
 * 
 * @see cz.semenko.doclet.XrefLink
 *
 */
public class XrefLinkTest {
	
	public static Logger logger = Logger.getLogger(XrefLinkTest.class);
	
	/**
	 * Set command line arguments
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		;
	}
	
	/**
	 * Run doclet and check whether there are any errors
	 */
	@Test
	public final void testRunDoclet() {
		HashSet<String> params = new HashSet<String>();
		params.add("-linksource");
		params.add("-doclet " + XrefLink.class.getName());
		com.sun.tools.javadoc.Main.execute(params.toArray(new String[0]));
	}
	
	/**
	 * Check whether links from javadoc classes are connected to xref classes
	 */
	@Test
	public final void testClassesAreConnected() {
		
	}
	
	/**
	 * Check whether links from javadoc methods are connected to xref classes
	 */
	@Test
	public final void testMethodsAreConnected() {
		
	}
	
	/**
	 * Check whether links from javadoc constructors are connected to xref classes
	 */
	@Test
	public final void testConstructorsAreConnected() {
		
	}
}
