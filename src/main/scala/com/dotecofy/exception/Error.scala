package com.dotecofy.exception

import scala.collection.mutable.ArrayBuffer

case class Error(kind: String, code: String, message: Option[String], details: Option[List[Error]])

object ErrorBuilder {
  val BAD_REQUEST = "bad_request"
  val NOT_FOUND = "not_found"
  val INTERNAL_ERROR = "internal_error"

  val INVALID_FORM_FIELD = "invalid_form_field"

  def badRequest(code: String, message: Option[String]): Error = {
    Error(BAD_REQUEST, code, message, Option(List()))
  }

  def invalidForm(code: String, message: Option[String]): Error = {
    Error(BAD_REQUEST, code, message, Option(List()))
  }

  def invalidForm(code: String, message: String, errors: List[Error]): Error = {
    Error(BAD_REQUEST, code, Option(message), Option(errors))
  }

  def fieldError(code: String, message: String): Error = {
    Error(INVALID_FORM_FIELD, code, Option(message), null)
  }

  def addFieldError(error: Error, code: String, message: Option[String]): Error = {
    val errors = ArrayBuffer.empty[Error]
    errors.appendAll(error.details.get)
    errors.append(Error(INVALID_FORM_FIELD, code, message, null))
    Error(error.kind, error.code, error.message, Option(errors.toList))
  }

  def internalError(code: String, message: String): Error = {
    Error(INTERNAL_ERROR, code, Option(message), null)
  }
}
