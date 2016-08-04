/**
 * Copyright (C) 2016 Luis Moral Guerrero <luis.moral@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.molabs.language.test;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import es.molabs.language.LanguageManager;

@RunWith(MockitoJUnitRunner.class)
public class LanguageManagerTest 
{	
	private final static Locale LOCALE_EN = new Locale("en");
	private final static Locale LOCALE_ES = new Locale("es");
		
	@Test
	public void testInitialization() throws Throwable
	{
		LanguageManager languageManager = new LanguageManager(LOCALE_EN);
		languageManager.addFile(getClass().getResource("/es/molabs/language/test/manager/test_en.properties"), LOCALE_EN);
		languageManager.addFile(getClass().getResource("/es/molabs/language/test/manager/test_es.properties"), LOCALE_ES);
		
		Throwable exception = null;
		
		// Checks that an IllegalStateException is thrown
		try
		{
			languageManager.getLocalizedKey("test.key.1", LOCALE_EN);
		}
		catch (Exception e)
		{
			exception = e;
		}
		Assert.assertEquals("Value must be [" + true + "].", true, exception.getClass() == IllegalStateException.class);
		
		// Initializes the manager
		languageManager.init();
		
		// Checks that the keys are loaded
		testLocalizeString(languageManager, "test.key.1", LOCALE_EN, "First");
		
		// Checks that its initialized
		boolean expectedValue = true;
		boolean value = languageManager.isInitialized();
		Assert.assertEquals("Value must be [" + expectedValue + "].", expectedValue, value);
				
		// Destroys the manager
		languageManager.destroy();
		
		// Checks that an IllegalStateException is thrown
		try
		{
			languageManager.getLocalizedKey("test.key.1", LOCALE_EN);
		}
		catch (Exception e)
		{
			exception = e;
		}
		Assert.assertEquals("Value must be [" + true + "].", true, exception.getClass() == IllegalStateException.class);
		
		// Checks that is not initialized
		expectedValue = false;
		value = languageManager.isInitialized();
		Assert.assertEquals("Value must be [" + expectedValue + "].", expectedValue, value);
		
		// Initializes the manager again
		languageManager.init();
		
		// Checks that the keys are loaded
		testLocalizeString(languageManager, "test.key.1", LOCALE_EN, "First");
		
		// Checks that its initialized
		expectedValue = true;
		value = languageManager.isInitialized();
		Assert.assertEquals("Value must be [" + expectedValue + "].", expectedValue, value);
		
		languageManager.destroy();
	}
	
	@Test
	public void testGetLocalizedKey() throws Throwable
	{
		LanguageManager languageManager = new LanguageManager(LOCALE_EN);
		languageManager.addFile(getClass().getResource("/es/molabs/language/test/manager/test_en.properties"), LOCALE_EN);
		languageManager.addFile(getClass().getResource("/es/molabs/language/test/manager/test_es.properties"), LOCALE_ES);
		languageManager.init();
		
		// Checks a value for LOCALE_EN
		testLocalizeString(languageManager, "test.key.1", LOCALE_EN, "First");
		
		// Checks a value for LOCALE_ES
		testLocalizeString(languageManager, "test.key.1", LOCALE_ES, "Primera");
		
		// Checks a value for a locale that does not exists so it gets the default locale value
		testLocalizeString(languageManager, "test.key.1", new Locale("fr"), "First");
		
		// Checks a value for a locale with language and country so it gets the value for that locale language instead 
		testLocalizeString(languageManager, "test.key.1", new Locale("es", "ES", "test"), "Primera");
		
		// Checks a value for a null locale so it gets the default locale value
		testLocalizeString(languageManager, "test.key.1", null, "First");
		
		languageManager.destroy();		
	}
	
	@Test
	public void testChangeDefaultLocale() throws Throwable
	{
		LanguageManager languageManager = new LanguageManager(LOCALE_EN);
		languageManager.addFile(getClass().getResource("/es/molabs/language/test/manager/test_en.properties"), LOCALE_EN);
		languageManager.addFile(getClass().getResource("/es/molabs/language/test/manager/test_es.properties"), LOCALE_ES);
		languageManager.init();
		
		// Checks that if a value for a locale does not exists gets the default locale value
		testLocalizeString(languageManager, "test.key.1", new Locale("fr"), "First");
		
		// Sets a new default locale
		languageManager.setDefaultLocale(LOCALE_ES);
		
		// Checks that the default locale value has changed to the new locale
		testLocalizeString(languageManager, "test.key.1", new Locale("fr"), "Primera");
		
		languageManager.destroy();
	}
	
	private void testLocalizeString(LanguageManager languageManager, String key, Locale locale, String expectedValue)
	{	
		String value = languageManager.getLocalizedKey(key, locale);
		Assert.assertEquals("Value must be [" + expectedValue + "].", expectedValue, value);
	}
}