/*******************************************************************************
 * Copyright 2010 Cees De Groot, Alex Boisvert, Jan Kotek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.mxro.thrd.jdbm2V22.htree;

import java.io.IOError;
import java.io.IOException;

import de.mxro.thrd.jdbm2V22.SecondaryHashMap;
import de.mxro.thrd.jdbm2V22.helper.JdbmBase;
import de.mxro.thrd.jdbm2V22.helper.SecondaryKeyHelper;


public class HTreeSecondaryMap<A,K,V> extends HTreeMap<A,Iterable<K>> implements SecondaryHashMap<A,K,V>{

	protected final JdbmBase<K,V > b;
	public HTreeSecondaryMap(HTree<A, Iterable<K>> tree, JdbmBase<K,V> b) {
		super(tree, true);
		this.b = b;
	}
	
	public V getPrimaryValue(K k) {		
		try {
			return b.find(k);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	public Iterable<V> getPrimaryValues(A a) {
		return SecondaryKeyHelper.translateIterable(b, get(a));
	}

}
