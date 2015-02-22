package net.d80harri.wr.ui.core;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class PresentationModelCommand<V> {
	private static final Logger logger = LoggerFactory.getLogger(PresentationModelCommand.class);
	
	public V start() {
		try {
			return call();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public Service<V> callAsync() {
		return new Service<V>(){

			@Override
			protected Task<V> createTask() {
				return new Task<V>() {

					@Override
					protected V call() throws Exception {
						return PresentationModelCommand.this.call();
					}
					
				};
			}
			
		};
	}
	
	protected abstract V call() throws Exception;
}
