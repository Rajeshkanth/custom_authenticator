## Custom authenticator

This project provides a custom authenticator for Keycloak-24.0.3, implementing custom logic for user login and registration processes.

## Table of Contents
Prerequisites
Installation
Configuration
Usage
Development
Contributing
License

## Prerequisites
Before you begin, ensure you have the following prerequisites:

Java Development Kit (JDK) 11 or higher

Apache Maven

A running instance of Keycloak (version 12.0.0 or higher is recommended)

## Installation

Clone the repository:

git clone https://github.com/Rajeshkanth/custom-authenticator.git

cd custom-keycloak-authenticator

## Build the project using Maven:

## mvn clean install

## Deploy the JAR file to your Keycloak instance:

Copy the generated JAR file from the target directory to the providers directory of your Keycloak server:

cp target/custom-authenticator.jar /opt/keycloak/providers/

`or`

## Building and deploying the jar files

`./script.sh`

this command will automatically run the `mvn clean package` and will move the jar files in the provider folder in target destination

## Restart the Keycloak server:

/opt/keycloak/bin/kc.sh stop

/opt/keycloak/bin/kc.sh start

## Configuration
Log in to the Keycloak admin console:

Open your web browser and go to `http://<your-keycloak-domain>/auth/admin/` and log in with your admin credentials.

Navigate to the appropriate realm:

Select the realm where you want to configure the custom authenticator.

## Create an Authentication Flow:

Go to Authentication -> `Flows`.

Click `Add` to create a new flow or select an existing flow to modify.

Add a new execution with the type Custom Authenticator.

Configure the custom authenticator:

Click on Actions -> `Add Execution`.

Select Custom Authenticator from the list.

Click `Save`.

## Set up the execution:

Click Actions -> Config.

Configure the authenticator as needed.

Click Save.

## Usage
Once configured, the custom authenticator will be used in the specified authentication flow for login and registration.

Login

Users attempting to log in will be directed through the custom authentication logic.

Registration

New users registering will also pass through the custom authentication process.

## Development

If you want to contribute to this project or customize it further, follow these steps:

Set up your development environment:

Ensure you have JDK 11 or higher and Apache Maven installed.

Clone the repository:

git clone https://github.com/your-username/custom-keycloak-authenticator.git

cd custom-keycloak-authenticator

## Implement your custom logic:

Modify the existing authenticator or add new ones in the src/main/java directory.

## Build the project:

`mvn clean install` or `mvn clean package`

Deploy and test:

Deploy the updated JAR file to your Keycloak instance and test your changes.

By running either `mvn clean packge` or `./script.sh` to build the jar file.

## Contributing
We welcome contributions to this project. Please open an issue or submit a pull request on GitHub if you have any improvements or bug fixes.

## License
This project is licensed under the MIT License.

