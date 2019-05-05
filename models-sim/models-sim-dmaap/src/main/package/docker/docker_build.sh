#!/bin/bash
#
# ============LICENSE_START=======================================================
#  Copyright (C) 2019 Nordix Foundation.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#

#
# Script to build a Docker file for the DMaaP simulator. The docker image
# generated by this script should NOT be placed in the ONAP nexus, it is
# only for testing purposes.
#

if [ -z "$DMAAP_SIM_HOME" ]
then
	DMAAP_SIM_HOME=`pwd`
fi

# Check for the dockerfile
if [ ! -f "$DMAAP_SIM_HOME/src/main/package/docker/Dockerfile" ]
then
	echo docker file "$DMAAP_SIM_HOME/src/main/package/docker/Dockerfile" not found
	exit 1
fi

# Check for the start script
if [ ! -f "$DMAAP_SIM_HOME/src/main/package/docker/dmaap-sim.sh" ]
then
	echo start script "$DMAAP_SIM_HOME/src/main/package/docker/dmaap-sim.sh" not found
	exit 1
fi

# Check for the tarball
tarball_count=`ls $DMAAP_SIM_HOME/target/policy-models-sim-dmaap-*-SNAPSHOT-tarball.tar.gz 2> /dev/null | wc | awk '{print $1}'`
if [ "$tarball_count" -ne "1" ]
then
	echo one and only one tarball should exist in the target directory
	exit 2
fi

# Set up the docker build
rm -fr $DMAAP_SIM_HOME/target/docker
mkdir $DMAAP_SIM_HOME/target/docker
cp $DMAAP_SIM_HOME/src/main/package/docker/Dockerfile $DMAAP_SIM_HOME/target/docker
cp $DMAAP_SIM_HOME/src/main/package/docker/dmaap-sim.sh $DMAAP_SIM_HOME/target/docker
cp $DMAAP_SIM_HOME/target/policy-models-sim-dmaap-*-SNAPSHOT-tarball.tar.gz $DMAAP_SIM_HOME/target/docker/policy-models-sim-dmaap-tarball.tar.gz

# Run the docker build
cd $DMAAP_SIM_HOME/target
docker build -t dmaap/simulator docker

