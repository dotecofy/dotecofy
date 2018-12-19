package com.dotecofy.workspace.feature

import com.dotecofy.models.Feature

trait FeatureServicesComponent {
  def load(index: Int, nb: Int): List[Feature]
}

object FeatureServices extends FeatureServicesComponent {

  def load(index: Int, nb: Int): List[Feature] = {
    /*Future {
      FeatureDAO.load(index, nb)
    }*/
    /* val res = featureRepository.load(index, nb)
     Await.result(res, 0 nanos)
     res foreach { features =>
       return features
     }*/

    //featureRepository.load(index, nb)
    Feature.findAll()
  }
}
