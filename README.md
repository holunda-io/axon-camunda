# Axon Camunda

[![Build Status](https://travis-ci.org/holunda-io/axon-camunda.svg?branch=master)](https://travis-ci.org/holunda-io/axon-camunda)


This library demonstrates usage of Camunda BPM engine for Saga-like execution 
of orchestration between different Axon Aggregates.

## Features

- Orchestration of Axon aggregates based on BPMN Messages and BPMN Signals
- Modelling of Orchestration using BPMN 2.x
- Implementation of mapping of the domain logic in Java / Kotlin
- Easy integration of the solution with other tools like Camunda Cockpit for operations and management

## Basic concept

AxonFramework is a fast-emerging and very promissing framework for building self-contained systems in Java. In doing so, 
it allows for an effective implementation of the CQRS ES architecture style and de-couples architectural design from 
strategy of system distribution and deployment.  


## Usage

### Implementing orchestration using BPMN

In order to implement the orchestration using BPMN, please add the following Apache Maven dependency to your project:

      <dependency>
          <groupId>io.holunda.axon</groupId>
          <artifactId>axon-camunda-core</artifactId>
          <version>0.0.1-SNAPSHOT</version>
      </dependendcy>
      
Now, two things need to be done. A BPMN process model containing the orchestration logic needs to be implemented.
This model should use the BPMN Intermediate Throwing Message Events each time an aggregate needs to be sent a Command.
The details of the event properties are:

* Choose **Implementation**: `Delegate Expression`
* Choose **Delegate Expression**: `${commandSender}`
* Choose a Message Name and remember it: e.G. `MESSAGE_ONE`
* Set the **Asynchronous Continuation** to `Asynchronous Before`

After doing this, provide a `CamundaAxonCommandFactory` implementation.  (As a part of the implementation 
of the `AbstractEventCommandFactory` which must be registered at `CamundaAxonEventCommandFactoryRegistry`, as shown below).
In doing so, implement the `command` method for the message defined in the process model. For example: 

      fun command(messageName: String, execution: DelegateExecution): Any {
        // create a mapper between payload variables and command objects based on the message name
        return when (messageName) {
          MESSAGE_ONE -> MyCommand(execution.getVariable("ID") as String, execution.getVariable("PAYLOAD") as Integer)
          else -> super.command(messageName, execution)
        }
      }

The receiving events from Aggregates can be proceeded by two means:
 
If the process instance is not aware of the state of the aggregate a signal can be used. (The process instance is 
just informed about the event, all instances will get all events / event broadcasting). Add corresponding 
Intermediate Catching Signal Event to your process model and remember the Signal name (e.G. SIGNAL_ONE). Now,
implement the `CamundaAxonCommandFactory` by mapping the incoming Axon event to the `CamundaEvent` (As a part 
of the implementation of the `AbstractEventCommandFactory` which must be registered 
at `CamundaAxonEventCommandFactoryRegistry`, as shown below):

      /**
       * Events
       */
      override fun event(payload: Any, metadata: MetaData): CamundaEvent? =
        when (payload) {
          is MyEvent -> {
            CamundaEvent("SIGNAL_ONE", mapOf<String, Any>("SOME_ID" to payload.someId))
          }
          else -> null
        }  

If the process instance is aware of the state of the aggregate, Intermediate Catching Message Event should be used 
(e.g. if a catching event must match the process instance which has sent the command in the past).
For doing so, the correlation id for identification of the Camunda process instance must be included into Axon Command, 
and this id must be passed to the emitted event and marked with the `@EventCorrelationId` annotation (see next chapter).
Add the corresponding Intermediate Catching Message Event to the process model and remember the Message name. 
The mapping is done in the same way it is done for the signal, but you have to set the `correlationVariableName`:

      override fun event(payload: Any, metadata: MetaData): CamundaEvent? =
        when (payload) {
          is MyEvent -> {
            CamundaEvent("MESSAGE_ONE", mapOf<String, Any>("SOME_ID" to payload.someId), "INSTANCE_ID")
          }
          else -> null
        }  



As a final step, the resulting implementation of the `AbstractEventCommandFactory` needs to be registered by 
the `CamundaAxonEventCommandFactoryRegistry`.    


      @Configuration
      open class MyConfiguration {
      
        @Autowired
        fun configure(registry: CamundaAxonEventCommandFactoryRegistry) {
          registry.register(object : AbstractEventCommandFactory(TravelProcess.KEY) {
                  
            /**
             * Commands
             */
            override fun command(messageName: String, execution: DelegateExecution): Any {
              // your command producer, see above
              ...
            }
                              
            /**
             * Events
             */
            override fun event(payload: Any, metadata: MetaData): CamundaEvent? {
              // your message and signal producer, see above
              ...
            }
          })
      }

### Correlating event messages to running process instances 

The `axon-camunda-annotations` is a library which can be used during the implementation of a 
self-contained system using aggregates, which needs to be orchestrated. In order to use this, 
please add the following Apache Maven dependency to your project:

      <dependency>
          <groupId>io.holunda.axon</groupId>
          <artifactId>axon-camunda-annotations</artifactId>
          <version>0.0.1-SNAPSHOT</version>
      </dependendcy>   

Now, you can use the `@EventCorrelationId` annotation to identify the field inside of the event, 
emitted by the Aggregate which contains the value of the variable used to correlate the incoming 
BPMN message event with the running orchestrating process instance.

Imagine your process instance has a string variable `BUSINESS_KEY`. By sending command to the aggregate, 
you include the value of this variable into the command:

     data class CreateSomethingCommand(@TargetAggregateIdentifier val businessKey: String)
     
The corresponding `CamundaAxonCommandFactory` looks like the following:

      fun command(messageName: String, execution: DelegateExecution): Any {
        return when (messageName) {
          MESSAGE_ONE -> CreateSomethingCommand(execution.getVariable("BUSINESS_KEY") as String)
          else -> super.command(messageName, execution)
        }
      }

Then, on the Aggregate side, the application of the command leads to an emission of an Event. For example:

      data class SomethingCreatedEvent(@EventCorrelationId val businessKey: String)
      
The corresponding `CamundaAxonEventFactory` looks like the following:


      override fun event(payload: Any, metadata: MetaData): CamundaEvent? =
        when (payload) {
          is SomethingCreatedEvent -> {
            CamundaEvent("MESSAGE_ONE", mapOf<String, Any>(), "BUSINESS_KEY")
          }
          else -> null
        }  
  
## ToDos 

- Implement General Singal catching process for setting process variables. 
- Support modeller templates
- Finish example (add airline, add car booking), add compensation
- Write more lib tests
- Write more docs
- More ITests 

## Bugs


