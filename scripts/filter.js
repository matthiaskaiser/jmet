function filter(message){

    message.setJMSPriority(3);
    print("Changed Priority")
    return message;

}

