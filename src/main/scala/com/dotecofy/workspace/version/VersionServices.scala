package com.dotecofy.workspace.feature

import scala.concurrent._
import scala.concurrent.duration._
import com.dotecofy.models.Version



object VersionServices  {

  def load(index: Int, nb: Int): List[Version] = {
    /*Future {
      FeatureDAO.load(index, nb)
    }*/
   /* val res = featureRepository.load(index, nb)
    Await.result(res, 0 nanos)
    res foreach { features =>
      return features
    }*/

    //featureRepository.load(index, nb)
    Version.findAll()
  }
}
