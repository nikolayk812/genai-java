# Generative AI with Testcontainers

This project demonstrates how to use Testcontainers to create a seamless development environment for building Generative AI applications.

## Project Structure

1. `1-hello-world`: Contains a simple example of using a language model to generate text.
2. `2-streaming`: Contains an example of using a language model to generate text in streaming mode.
3. `3-chat`: Contains an example of using a language model to generate text in a chat application.
4. `4-vision-model`: Contains an example of using a vision model to generate text from images.
5. `5-augmented-generation`: Contains an example of augmenting the prompt with additional information to generate more accurate text.
6. `6-embeddings`: Contains an example of generating embeddings from text and calculating similarity between them.

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

To run the examples, navigate to the desired directory and run the `run` task. For example, to run the `1-hello-world` example:

```sh
cd 1-hello-world
../gradlew run