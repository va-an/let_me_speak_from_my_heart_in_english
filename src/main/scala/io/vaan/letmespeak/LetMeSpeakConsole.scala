package io.vaan.letmespeak

import io.vaan.letmespeak.service.PracticeConsole
import io.vaan.letmespeak.util.ResourceUtils.fetchVerbs

import scala.util.{Failure, Random, Success, Try}

object LetMeSpeakConsole {
  def main(args: Array[String]): Unit = {
    val filename = args(0)
    val allVerbs = fetchVerbs(filename)

    val from = Try(args(1).toInt) match {
      case Failure(_) => 0
      case Success(value) => value
    }

    val to = Try(args(2).toInt) match {
      case Failure(_) => allVerbs.size
      case Success(value) => value
    }

    val verbs = Random.shuffle(
      allVerbs.slice(from, to)
    )

    PracticeConsole.play(verbs)
  }
}
