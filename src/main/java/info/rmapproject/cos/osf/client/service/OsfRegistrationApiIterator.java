/*******************************************************************************
 * Copyright 2017 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
package info.rmapproject.cos.osf.client.service;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.github.jasminb.jsonapi.ResourceList;

import info.rmapproject.cos.osf.client.model.LightRegistration;

/**
 * Retrieves and iterates over OSF Registration data.
 *
 * @author khanson
 */
public class OsfRegistrationApiIterator extends OsfApiIterator<LightRegistration> {
	
	/** The list of IDs to iterate over. */
	protected ResourceList<LightRegistration> ids = null;
	
    /**
     * Instantiates a new osf registration api iterator.
     *
     * @param filters the filters
     */
    public OsfRegistrationApiIterator(Map<String, String> params){
    	super(params);
    	loadBatch(); 
    }
    	
	/**
	 * Load batch of OSF data from API using parameters defined.
	 */
    @Override
	protected void loadBatch() {
		position = 0;
		try {
			incrementPageNumberParam();
    		ids =(ResourceList<LightRegistration>) osfClient.getRegistrationIds(params);
			this.recordRetrievedDate=new DateTime(DateTimeZone.UTC);
		} catch(Exception e){
			LOG.error("Could not load list of records to iterate over.");
			throw new RuntimeException("Could not load list of records to iterate over.", e);
		}	
    }
        
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public LightRegistration next() {
		LightRegistration registration = null;
		
		try {			
			while(registration==null && hasNext()) {

				if ((ids==null || position==ids.size())){
					loadBatch();
				}
				//load next
				registration = ids.get(position);
				position=position+1;
			} 
		} catch (Exception e){
			//load failed
			throw new RuntimeException("Iterator failed to load Record for import",e);
		}

		if (registration==null){
			throw new RuntimeException("No more Registration records available in this batch");
		}		
		return registration;
	}


	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
    	int size = ids.size();
    	String next = ids.getNext();
    	    	
		return (position<(size) || next!=null);
	}
    	
}