package cz.semenko.word.database.tests;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import cz.semenko.word.sleeping.MemoryCleaner;

public class TestMemoryCleaner extends TestCase {

	public void testCleanMemoryFromRedundantAssociations () throws Exception {
		MemoryCleaner memoryCleaner = MemoryCleaner.getInstance();
		memoryCleaner.cleanMemoryFromRedundantObjects();
	}
}
