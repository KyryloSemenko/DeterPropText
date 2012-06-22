<?xml version="1.0" encoding="UTF-8"?>
<core:Model xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:core="org.eclipse.jwt/core" xmlns:data="org.eclipse.jwt/data" xmlns:organisations="org.eclipse.jwt/organisations" xmlns:processes="org.eclipse.jwt/processes" name="ThoughtsMergeRules" author="Semenko" version="1.1" description="Zde zkusim vymodelovat postup pro rozhodovani, jake prvky Thoughts oznacovat pro spojeni." fileversion="1.0.0">
  <subpackages name="Applications">
    <ownedComment text="The standard package for applications"/>
  </subpackages>
  <subpackages name="Roles">
    <ownedComment text="The standard package for roles"/>
  </subpackages>
  <subpackages name="Data">
    <ownedComment text="The standard package for data"/>
    <subpackages name="Datatypes">
      <ownedComment text="The standard package for datatypes"/>
      <elements xsi:type="data:DataType" name="URL"/>
      <elements xsi:type="data:DataType" name="dioParameter"/>
      <elements xsi:type="data:DataType" name="qualifier"/>
      <elements xsi:type="data:DataType" name="searchquery"/>
      <elements xsi:type="data:DataType" name="filename"/>
    </subpackages>
    <subpackages name="cz.semenko.word.aware">
      <elements xsi:type="data:DataType" name="Vector&lt;Thought>" icon=""/>
      <elements xsi:type="data:Data" name="thoughts2" icon="" value="thoughts2" dataType="//@subpackages.2/@subpackages.1/@elements.0"/>
    </subpackages>
  </subpackages>
  <elements xsi:type="processes:Activity" name="Thougth Merge Rules">
    <ownedComment text="This is a basic activity"/>
    <nodes xsi:type="processes:InitialNode" out="//@elements.0/@edges.1"/>
    <nodes xsi:type="processes:FinalNode"/>
    <nodes xsi:type="processes:Action" name="Nacist blok prvku Thought" in="//@elements.0/@edges.1" out="//@elements.0/@edges.0" outputs="//@subpackages.2/@subpackages.1/@elements.1"/>
    <nodes xsi:type="processes:Action" name="getObjectsToRelation(thoughts2, objectsCreationDepth)" in="//@elements.0/@edges.0" inputs="//@subpackages.2/@subpackages.1/@elements.1"/>
    <nodes xsi:type="processes:Action" name="get"/>
    <edges source="//@elements.0/@nodes.2" target="//@elements.0/@nodes.3"/>
    <edges source="//@elements.0/@nodes.0" target="//@elements.0/@nodes.2"/>
  </elements>
  <elements xsi:type="organisations:Role" name="AplikaceSlovicka"/>
</core:Model>
