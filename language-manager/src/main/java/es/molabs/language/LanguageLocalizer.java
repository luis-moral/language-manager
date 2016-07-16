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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import es.molabs.io.utils.NodePropertiesBundle;
import es.molabs.properties.NodePropertiesToken;

public class LanguageLocalizer extends NodePropertiesBundle 
{
	private Locale locale = null;
	
	public LanguageLocalizer(Locale locale, NodePropertiesToken...tokenList)
	{
		this(locale, Arrays.asList(tokenList));
	}
	
	public LanguageLocalizer(Locale locale, List<NodePropertiesToken> tokenList)
	{
		super(tokenList);
		
		this.locale = locale;
	}
	
	public Locale getLocale()
	{
		return locale;
	}
}