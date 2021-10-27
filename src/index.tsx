import * as React from "react";
import * as ReactDOM from "react-dom";
import Prbot from "./components/PrBot";
import { SnackbarProvider } from 'notistack';



ReactDOM.render(
  <SnackbarProvider>
    <div>

    <Prbot/>

    </div>
  </SnackbarProvider>
,

  document.getElementById("root")
);