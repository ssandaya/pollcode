package com.veeder.ava.polldata

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

/**
 * Created by sandaya on 7/2/2017.
 */

@AutoClone
@EqualsAndHashCode
@Builder(builderStrategy=SimpleStrategy)
class ProbeSamples extends ProbeSample5sec {
}
