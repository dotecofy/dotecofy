package com.dotecofy.workspace.workspace

import java.time.ZonedDateTime

case class WorkspaceDB(
                        id: Int,
                        signature: String,
                        name: String,
                        description: Option[String] = None,
                        createdDate: ZonedDateTime,
                        updatedDate: Option[ZonedDateTime] = None)

case class WorkspaceSrv(
                         signature: String,
                         name: String,
                         description: String,
                         createdDate: ZonedDateTime,
                         updatedDate: ZonedDateTime)
