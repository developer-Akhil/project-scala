package com.etl.gcp.model

import java.util.Date
import io.getquill.context.sql.SqlContext

case class CheckSumTable(corelationid:String,
                         filepath:String,
                         filename:String,
                         checksumval:String,
                         isdup:Boolean,
                         ts:String,
                         lookupkey:String )


trait Operators {
  this: SqlContext[_, _] =>
  implicit class DateComparison(left: Date) {
    def >(right: Date) = quote(infix"$left > $right".as[Boolean])

    def <(right: Date) = quote(infix"$left < $right".as[Boolean])

    def >=(right: Date) = quote(infix"$left >= $right".as[Boolean])

    def <=(right: Date) = quote(infix"$left <= $right".as[Boolean])
  }


  implicit class StringComparison(left: String) {
    def >(right: String) = quote(infix"$left > $right".as[Boolean])

    def <(right: String) = quote(infix"$left < $right".as[Boolean])

    def >|=(right: String) = quote(infix"$left >= $right".as[Boolean])

    def <|=(right: String) = quote(infix"$left <= $right".as[Boolean])
  }

  implicit class DateQuotes(left: Date) {
    def >(right: Date) = quote(infix"$left > $right".as[Boolean])
    def <(right: Date) = quote(infix"$left < $right".as[Boolean])
  }
}