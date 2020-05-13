package io.vaan.letmespeak.util

import io.vaan.letmespeak.dto.Verb

import scala.io.Source

object ResourceUtils {

  /**
   * Open file with verbs from resources
   * @param fileForOpen - filename
   * @return Seq[Verb]
   */
  def fetchVerbs(fileForOpen: String): List[Verb] =
    Source
      .fromResource(fileForOpen)
      .getLines
      .toList
      .map { x =>
        val split = x.split("\\|")

        Verb(
          word = split(3),
          v1 = split(0),
          v2 = split(1),
          v3 = split(2))
      }
}
