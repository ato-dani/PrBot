import React from "react";
import "@testing-library/jest-dom/extend-expect";
import PrBot from "./index";
import { fireEvent, render, wait } from "@testing-library/react";
import { SnackbarProvider } from "notistack";
import axios from "axios";
import { APIPath } from "../../config/ApiConfig";

jest.mock("axios");
describe("<PrBot/>", () => {
  beforeEach(() => {
    axios.get.mockResolvedValue({
      data: {
        success_status: true,
        message: "",
      },
    });
  });
  const Wrapper = (
    <SnackbarProvider>
      <div>
        <PrBot></PrBot>
      </div>
    </SnackbarProvider>
  );

  it("Submits to email correctly", async () => {
    const { getByTestId, getByText } = render(Wrapper);
    await wait();
    // change message and title
    const title = getByTestId("title");
    const message = getByTestId("message");
    fireEvent.change(title, { target: { value: "This is a title" } });
    fireEvent.change(message, { target: { value: "This is a message" } });
    await wait();
    // open dialogue and enter email info
    const twitterButton = getByText("Email");
    fireEvent.click(twitterButton);
    const smtpServer = getByTestId("smtpServer");
    const smtpPort = getByTestId("smtpPort");
    const emailAddress = getByTestId("emailAddress");
    const password = getByTestId("password");
    const destEmailAddress = getByTestId("destEmailAddress");
    fireEvent.change(smtpServer, { target: { value: "smtp.gmail.com" } });
    fireEvent.change(smtpPort, { target: { value: "587" } });
    fireEvent.change(emailAddress, { target: { value: "test@gmail.com" } });
    fireEvent.change(password, { target: { value: "test" } });
    fireEvent.change(destEmailAddress, { target: { value: "dest@gmail.com" } });
    fireEvent.click(getByText("Add Email"));
    // add another destination email
    fireEvent.change(destEmailAddress, {
      target: { value: "dest2@gmail.com" },
    });
    fireEvent.click(getByText("Add Email"));
    await wait();
    const submitButton = getByText("Submit");
    axios.get.mockClear();
    // button no longer disabled
    expect(submitButton).not.toBeDisabled();
    fireEvent.click(submitButton);
    await wait();
    //check if submit is called with correct query
    expect(axios.get).toHaveBeenCalledWith(
      APIPath.SUBMIT_EMAIL +
        "?email_from=test@gmail.com&password=test&title=This is a title&message=This is a message&smtp_server=smtp.gmail.com&smtp_port=587&dest_emails=dest@gmail.com,dest2@gmail.com"
    );
  });
});
