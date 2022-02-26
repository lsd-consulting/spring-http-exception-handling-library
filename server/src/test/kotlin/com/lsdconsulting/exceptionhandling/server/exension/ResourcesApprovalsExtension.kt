package com.lsdconsulting.exceptionhandling.server.exension

import com.oneeyedmen.okeydoke.ApproverFactories.fileSystemApproverFactory
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import java.io.File

class ResourcesApprovalsExtension : ApprovalsExtension(fileSystemApproverFactory(File("src/test/resources/data"), ".json"))
