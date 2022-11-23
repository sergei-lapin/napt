package com.slapin.sample.anvil;

import com.slapin.napt.sample.anvil.AnvilComponent;
import com.slapin.napt.sample.anvil.DaggerAnvilComponent;

public class AnvilComponentBridge {

  public static AnvilComponent create() {
    return DaggerAnvilComponent.create();
  }
}
