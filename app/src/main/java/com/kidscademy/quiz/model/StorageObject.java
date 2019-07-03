package com.kidscademy.quiz.model;

import com.kidscademy.quiz.app.Storage;


/**
 * A storage object is loaded from application external storage has a life span controlled by {@link StorageBase} instance.
 * A storage object has fields persisted to application storage; optional, storage object may have transient fields.
 * 
 * @author Iulian Rotaru
 */
public interface StorageObject
{
  /**
   * Hook called after storage object instance creation allowing for transient fields initialization.
   * 
   * @param storage parent storage instance.
   */
  void onCreate(Storage storage);
}
