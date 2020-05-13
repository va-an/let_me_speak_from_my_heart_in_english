package io.vaan.letmespeak.util

import io.vaan.letmespeak.dto.Verb

import scala.io.Source

object ResourceUtils {
  def fetchVerbs(fileForOpen: String): Seq[Verb] =
    Source
      .fromResource(fileForOpen)
      .getLines
      .toSeq
      .map { x =>
        val split = x.split("\\|")

        Verb(
          word = split(3),
          v1 = split(0),
          v2 = split(1),
          v3 = split(2))
      }
}
