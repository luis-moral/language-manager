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
package es.molabs.language;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.molabs.properties.NodePropertiesToken;

public class LanguageManager 
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Locale defaultLocale = null;
	private List<NodePropertiesToken> tokenList = null;
	
	private Map<Locale, LanguageLocalizer> localizerMap = null;	
	private Lock localizerLock = null;
	
	private boolean initialized;	
	
	public LanguageManager(Locale defaultLocale, NodePropertiesToken...tokenList)
	{
		this(defaultLocale, Arrays.asList(tokenList));
	}
	
	public LanguageManager(Locale defaultLocale, List<NodePropertiesToken> tokenList)
	{	
		this.defaultLocale = defaultLocale;
		this.tokenList = tokenList;
		
		localizerMap = new HashMap<Locale, LanguageLocalizer>();
		localizerLock = new ReentrantLock(true);
		
		initialized = false;		
	}
	
	public void init()
	{		
		if (!initialized)
		{
			try
			{
				Iterator<LanguageLocalizer> iterator = localizerMap.values().iterator();
				while (iterator.hasNext())
				{
					iterator.next().init();
				}
			}
			catch (IOException IOe)
			{
				throw new IllegalArgumentException(IOe);
			}
			
			// Sets the manager as initialized
			initialized = true;
			
			logger.info("Initialized.");
		}
		else
		{
			logger.warn("Already initialized.");
		}
	}
	
	public void destroy()
	{
		if (initialized)
		{
			// Sets the manager as not initialized
			initialized = false;
			
			try
			{
				Iterator<LanguageLocalizer> iterator = localizerMap.values().iterator();
				while (iterator.hasNext())
				{
					iterator.next().destroy();
				}
			}
			catch (IOException IOe)
			{
				logger.error(IOe.getLocalizedMessage(), IOe);
			}
			
			logger.info("Destroyed.");
		}
		else
		{
			logger.warn("Already destroyed.");
		}
	}
	
	public boolean isInitialized()
	{
		return initialized;
	}
	
	public Locale getDefaultLocale()
	{
		return defaultLocale;
	}
	
	public void setDefaultLocale(Locale defaultLocale)
	{
		this.defaultLocale = defaultLocale;
	}
	
	public void addFile(URL resource, Locale locale) throws IOException
	{
		getLocalizer(locale).addFile(resource);
	}
	
	public void addFile(URL resource, String encoding, Locale locale) throws IOException
	{
		getLocalizer(locale).addFile(resource, encoding);
	}
	
	public String getLocalizedKey(String key, Locale locale)
	{
		if (!initialized) throw new IllegalStateException("Not initialized.");
		
		return getLocalizer(normalizeLocale(locale)).getString(key);
	}
	
	public Locale normalizeLocale(Locale locale)
	{
		// If the locale does not exists in the locale map
		if (!localizerMap.containsKey(locale))
		{		
			// Gets the locale lookupList, if the locale is null returns the default locale because LocaleUtils.localeLookupList returns an empty list
			List<Locale> lookupList = LocaleUtils.localeLookupList((locale != null ? locale : defaultLocale), defaultLocale);
						
			// For each locale in the list, looks for the key till is not null of the iterator ends			
			Iterator<Locale> iterator = lookupList.iterator();
			
			while (iterator.hasNext())
			{
				Locale lookupLocale = iterator.next();
				
				if (localizerMap.containsKey(lookupLocale))
				{
					locale = lookupLocale;
					
					break;
				}			
			}
		}
		
		return locale;
	}
	
	public Set<Locale> getLocaleSet()
	{
		return localizerMap.keySet();
	}
	
	private LanguageLocalizer getLocalizer(Locale locale)
	{
		LanguageLocalizer localizer = null;
		
		localizerLock.lock();
		
		try
		{
			// Gets the localizer in the map
			localizer = localizerMap.get(locale);
			
			// If it does not exists
			if (localizer == null)
			{
				// Creates a new one
				localizer = new LanguageLocalizer(locale, tokenList);
				
				// Adds it to the map
				localizerMap.put(locale, localizer);
			}
		}
		finally
		{
			localizerLock.unlock();
		}
		
		return localizer;
	}
}
