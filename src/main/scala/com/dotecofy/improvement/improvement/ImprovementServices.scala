package com.dotecofy.improvement.improvement

import com.dotecofy.models.Improvement

object ImprovementServices {

  def load(index: Int, nb: Int): List[Improvement] = {
    /*Future {
      FeatureDAO.load(index, nb)
    }*/
    /* val res = featureRepository.load(index, nb)
     Await.result(res, 0 nanos)
     res foreach { features =>
       return features
     }*/

    //featureRepository.load(index, nb)
    Improvement.findAll()
  }
}
