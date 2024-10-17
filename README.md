# Generative AI with Testcontainers

This project demonstrates how to use Testcontainers to create a seamless development environment for building Generative AI applications.

## Project Structure

1. `hello-world`: Contains a simple example of using a language model to generate text.
2. `chat-streaming`: Contains an example of using a language model to generate text in streaming mode.
3. `vision-model`: Contains an example of using a vision model to generate text from images.
4. `chat`: Contains an example of using a language model to generate text in a chat application.
5. `augmented-generation`: Contains an example of augmenting the prompt with additional information to generate more accurate text.

## Prerequisites

- Java 21 or higher
- Docker

## Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/ilopezluna/generative-ai-testcontainers.git
    cd generative-ai-testcontainers
    ```

2. Build the project:
    ```sh
    ./gradlew build
    ```

## Running the Examples

To run the examples, navigate to the desired directory and run the `run` task. For example, to run the `hello-world` example:

```sh
cd hello-world
../gradlew run
```
