#!groovy

@Library('pipelib')
import org.veupathdb.lib.Builder

node('centos8') {

  def builder = new Builder(this)

  builder.gitCline()
  builder.buildContainers([[ name: 'sequence-retrieval' ]])

}
