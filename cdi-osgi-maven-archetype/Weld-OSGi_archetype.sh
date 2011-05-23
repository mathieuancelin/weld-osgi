#! /bin/bash
#

#Creation script for Weld-OSGi application
clear;

groupIdRegex='^[a-zA-Z][a-zA-Z0-9_]*([.][a-zA-Z][a-zA-Z0-9_]*)*$'
artifactIdRegex='^[a-zA-Z][a-zA-Z0-9_]*([-][a-zA-Z][a-zA-Z0-9_]*)*$'
versionRegex='^[0-9]+(\.[0-9]+)?(\.[0-9]+)?(\-[a-zA-Z0-9_]+)?$'

testRegex() {
    while :
    do
        echo -n "value="
        read value
        if [[ $value =~ $groupIdRegex ]]
        then
            echo "compliant"
            echo $value
        else
            echo "non compliant"
            echo $value
        fi
    done
}

configureApplication() {
    echo -n "Application group ID: "
    read appGroupId
    validateAppGroupId
    echo -n "Application artifact ID: "
    read appArtifactId
    validateAppArtifactId
    echo -n "Application version: (1.0-SNAPSHOT): "
    read appVersion
    validateAppVersion
    echo
}

configureContainer() {
    echo -n "Container group ID: ("$appGroupId"): "
    read containerGroupId
    validateContainerGroupId
    echo -n "Container artifact ID: ("$appArtifactId"-container): "
    read containerArtifactId
    validateContainerArtifactId
    echo -n "Container version: ("$appVersion"): "
    read containerVersion
    validateContainerVersion
    echo
}

configureBundles() {
    echo -n "How many bundles: (0): "
    read nbBundles
    validateNbBundles

    for(( i=1; i<= $nbBundles; i++ ))
    do
        echo "================================================================"
        echo "Bundle "$i" configuration"
        echo
        echo -n "Bundle group ID: ("$appGroupId"): "
        read bundleGroupId[$i]
        validateBundleGroupId
        echo -n "Bundle artifact ID: ("$appArtifactId"-bundle"$i"): "
        read bundleArtifactId[$i]
        validateBundleArtifactId
        echo -n "Bundle version: ("$appVersion"): "
        read bundleVersion[$i]
        validateBundleVersion
        echo -n "Bundle package: ("$appGroupId"): "
        read bundlePackage[$i]
        validateBundlePackage
    done
}

validConfiguration() {
    echo "================================================================"
    echo "Configuration validation"
    echo
    echo -n "Application "
    echo "("$appGroupId"."$appArtifactId"-"$appVersion")"
    echo -n "Container "
    echo "("$containerGroupId"."$containerArtifactId"-"$containerVersion")"
    for(( i=1; i<= $nbBundles; i++ ))
    do
        echo -n "Bundle "
        echo "("${bundleGroupId[$i]}"."${bundleArtifactId[$i]}"-"${bundleVersion[$i]}"@"${bundlePackage[$i]}")"
    done
    echo "Do you want to generate these Maven projects?"
    while [[ $valid != y ]]
    do
        echo -n "(y or n): "
        read valid
        if [[ $valid == n ]]
        then
            exit 1
        fi
    done
}

validateAppGroupId() {
    if [[ $appGroupId =~ $groupIdRegex ]]
    then
        echo $appGroupId
    else
        echo "Malformed group ID!"
        exit 11
    fi
}

validateAppArtifactId() {
    if [[ $appArtifactId =~ $artifactIdRegex ]]
    then
        echo $appArtifactId
    else
        echo "Malformed artifact ID!"
        exit 12
    fi
}

validateAppVersion() {
    if [[ $appVersion == "" ]]
    then
        appVersion="1.0-SNAPSHOT"
        echo $appVersion
    elif [[ $appVersion =~ $versionRegex ]]
    then
        echo $appVersion
    else
        appVersion="1.0-SNAPSHOT"
        echo "Malformed version ! Assume: "$appVersion
    fi
}

validateContainerGroupId() {
    if [[ $containerGroupId == "" ]]
    then
        containerGroupId=$appGroupId
        echo $containerGroupId
    elif [[ $containerGroupId =~ $groupIdRegex ]]
    then
        echo $containerGroupId
    else
        containerGroupId=$appGroupId
        echo "Malformed group ID ! Assume: "$containerGroupId
    fi
}

validateContainerArtifactId() {
    if [[ $containerArtifactId == "" ]]
    then
        containerArtifactId=$appArtifactId"-container"
        echo $containerArtifactId
    elif [[ $containerArtifactId =~ $artifactIdRegex ]]
    then
        echo $containerArtifactId
    else
        containerArtifactId=$appArtifactId"-container"
        echo "Malformed artifact ID ! Assume: "$containerArtifactId
    fi
}

validateContainerVersion() {
    if [[ $containerVersion == "" ]]
    then
        containerVersion=$appVersion
        echo $containerVersion
    elif [[ $containerVersion =~ $versionRegex ]]
    then
        echo $containerVersion
    else
        containerVersion=$appVersion
        echo "Malformed version ! Assume: "$containerVersion
    fi
}

validateNbBundles() {
    if [[ $nbBundles == "" ]]
    then
        nbBundles="0"
        echo $nbBundles
    elif [[ $nbBundles =~ ^[0-9]+$ ]]
    then
        echo $nbBundles
    else
        nbBundles="0"
        echo "Invalid number ! Assume: "$nbBundles
    fi
}

validateBundleGroupId() {
    if [[ ${bundleGroupId[$i]} == "" ]]
    then
        bundleGroupId[$i]=$appGroupId
        echo ${bundleGroupId[$i]}
    elif [[ ${bundleGroupId[$i]} =~ $groupIdRegex ]]
    then
        echo ${bundleGroupId[$i]}
    else
        bundleGroupId[$i]=$appGroupId
        echo "Malformed group ID ! Assume: "${bundleGroupId[$i]}
    fi
}

validateBundleArtifactId() {
    if [[ ${bundleArtifactId[$i]} == "" ]]
    then
        bundleArtifactId[$i]=$appArtifactId"-bundle"$i
        echo ${bundleArtifactId[$i]}
    elif [[ ${bundleArtifactId[$i]} =~ $artifactIdRegex ]]
    then
        echo ${bundleArtifactId[$i]}
    else
        bundleArtifactId[$i]=$appArtifactId"-bundle"$i
        echo "Malformed artifact ID ! Assume: "${bundleArtifactId[$i]}
    fi
}

validateBundleVersion() {
    if [[ ${bundleVersion[$i]} == "" ]]
    then
        bundleVersion[$i]=$appVersion
        echo ${bundleVersion[$i]}
    elif [[ ${bundleVersion[$i]} =~ $versionRegex ]]
    then
        echo ${bundleVersion[$i]}
    else
        bundleVersion[$i]=$appVersion
        echo "Malformed version ! Assume: "${bundleVersion[$i]}
    fi
}

validateBundlePackage() {
    if [[ ${bundlePackage[$i]} == "" ]]
    then
        bundlePackage[$i]=${bundleGroupId[$i]}
        echo ${bundlePackage[$i]}
    elif [[ ${bundlePackage[$i]} =~ $groupIdRegex ]]
    then
        echo ${bundlePackage[$i]}
    else
        bundlePackage[$i]=${bundleGroupId[$i]}
        echo "Malformed package ! Assume: "${bundlePackage[$i]}
    fi
}

generate() {
    echo "================================================================"
    echo "Application generation"
    echo
    mvn -q archetype:generate  -DinteractiveMode=false \
                            -DarchetypeGroupId=org.osgi.cdi \
                            -DarchetypeArtifactId=weld-osgi-application-archetype \
                            -DgroupId=$appGroupId \
                            -DartifactId=$appArtifactId \
                            -Dversion=$appVersion \
                            -DcontainerGroupId=$containerGroupId \
                            -DcontainerArtifactId=$containerArtifactId \
                            -DcontainerVersion=$containerVersion
    cd ./$appArtifactId
    for(( i=1; i<= $nbBundles; i++ ))
    do
        echo "================================================================"
        echo "Bundle "$i" generation"
        echo
        mvn -q archetype:generate  -DinteractiveMode=false \
                                -DarchetypeGroupId=org.osgi.cdi \
                                -DarchetypeArtifactId=cdi-osgi-bundle-archetype \
                                -DgroupId=${bundleGroupId[$i]} \
                                -DartifactId=${bundleArtifactId[$i]} \
                                -Dversion=${bundleVersion[$i]} \
                                -DbundlePackage=${bundlePackage[$i]} \
                                -DappGroupId=$appGroupId \
                                -DappArtifactId=$appArtifactId \
                                -DappVersion=$appVersion
    done
}

link() {
    chmod +x run.sh
    if [[ nbBundles != 0 ]]
    then
        echo "================================================================"
        echo "Updating pom.xml file ..."
        echo
        for(( i=1; i<= $nbBundles; i++ ))
        do
            pom[$i]="<dependency><groupId>${bundleGroupId[$i]}</groupId><artifactId>${bundleArtifactId[$i]}</artifactId><version>${bundleVersion[$i]}</version></dependency>"
        done
        cd ./$containerArtifactId
        for(( i=1; i<= $nbBundles; i++ ))
        do
            sed -i "17i\\${pom[$i]}" pom.xml
        done
        echo "================================================================"
        echo "Updating felix.xml file ..."
        echo
        for(( i=1; i<= $nbBundles; i++ ))
        do
            felix[$i]="<include>"${bundleGroupId[$i]}":"${bundleArtifactId[$i]}"</include>"
        done
        cd ./src/main/assembly
        for(( i=1; i<= $nbBundles; i++ ))
        do
            sed -i "15i\\${felix[$i]}" felix.xml
        done
        echo "================================================================"
        echo "Updating config.properties file ..."
        echo
        for(( i=1; i<= $nbBundles; i++ ))
        do
            config[$i]="file:modules/${bundleArtifactId[$i]}-${bundleVersion[$i]}.jar"
        done
        cd ../resources
        for(( i=1; i<= $nbBundles; i++ ))
        do
            sed -i "9i\\${config[$i]} \\\\" config.properties
        done
    fi
}

main() {
    echo "================================================================"
    echo "=               Weld OSGi application generator                ="
    echo "================================================================"
    echo
    #testRegex
    echo "================================================================"
    echo "Global application configuration"
    echo
    configureApplication
    echo "================================================================"
    echo "Application container configuration"
    echo
    configureContainer
    echo "================================================================"
    echo "Application bundles configuration"
    echo
    configureBundles
    echo "================================================================"
    echo "Defined configuration"
    echo
    validConfiguration
    echo "================================================================"
    echo "Generating Maven projects ..."
    echo
    generate
    echo "================================================================"
    echo "Linking generated Maven projects ..."
    echo
    link
}

main