/* Copyright 2016 Aron Heinecke. See the COPYRIGHT
 * file at the top-level directory of this distribution.
 * 
 * Licensed under the MIT license
 * <LICENSE-MIT or http://opensource.org/licenses/MIT>.
 * This file may not be copied, modified, or distributed
 * except according to those terms.
 */
package me.Aron.Heinecke.VocableTrainer.lib;

/**
 * Simple error & value wrapper
 * @author Aron Heinecke
 */
public class DBError<T> {
	public boolean isError;
	public Exception error;
	public T value;
	
	public DBError(){
		isError = false;
		error = null;
		this.value = null;
	}
	public DBError(T value){
		this.value = value;
		this.isError = false;
		this.error = null;
	}
	public DBError(Exception e){
		error = e;
		isError = true;
	}
}
