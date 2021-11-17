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
        access_token: "dflsfljlekjldfdldklfsd.dlfdfd.dfsdfdds",
        access_token_secret: "fdfldlerwerfdsfsdl.efsdfewew.fdfsdfsdafasfsdf",
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
  it("Signs in and post to Discord correctly", async () => {
    const { getByTestId, getByText } = render(Wrapper);
    await wait();
    // change message and title
    const title = getByTestId("title");
    const message = getByTestId("message");
    fireEvent.change(title, { target: { value: "This is a title" } });
    fireEvent.change(message, { target: { value: "This is a message" } });
    await wait();

    // open dialogue and sign in
    const twitterButton = getByText("Discord");
    fireEvent.click(twitterButton);
    fireEvent.click(getByText("Sign in"));
    await wait();
    expect(axios.get).toHaveBeenCalledWith(APIPath.SIGN_IN_DISCORD);

    const submitButton = getByText("Submit");
    axios.get.mockClear();
    // button no longer disabled
    expect(submitButton).not.toBeDisabled();
    fireEvent.click(submitButton);
    await wait();
    //check if submit is called with correct query
    expect(axios.get).toHaveBeenCalledWith(
      APIPath.SUBMIT_DISCORD +
        "?access_token=dflsfljlekjldfdldklfsd.dlfdfd.dfsdfdds&access_token_secret=fdfldlerwerfdsfsdl.efsdfewew.fdfsdfsdafasfsdf&title=This is a title&message=This is a message"
    );
  });
});
