package cloud.dest.sbf.auth.server

import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.FeatureDef

class TestAuthServerAPI extends CornichonFeature {
  override def feature: FeatureDef = Feature("Test Auth Server") {
    Scenario("Auth Server is up and running"){

      When I get("http://localhost:8080/auth/auth")

      Then assert status.is(200)
      And assert body.is("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoxLCJuYmYiOjE0MzE1MjA0MjF9.VmfmoqRbRvna9lfpCx4lXf96eD_X_woBM0twLjBGLlQ")

    }
  }
}
